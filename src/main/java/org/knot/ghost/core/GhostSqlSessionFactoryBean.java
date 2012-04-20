package org.knot.ghost.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.knot.ghost.core.builder.xml.GhostXMLConfigBuilder;
import org.knot.ghost.core.session.GhostSqlSessionFactoryBuilder;
import org.knot.ghost.datasources.IGhostDataSource;
import org.knot.ghost.router.IGhostRouter;
import org.knot.ghost.router.support.MyBatisRoutingFact;
import org.knot.ghost.support.execution.DefaultConcurrentRequestProcessor;
import org.knot.ghost.support.utils.CollectionUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostSqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ApplicationEvent> {

    private IGhostRouter<MyBatisRoutingFact> router;

    // 每个executor中线程池大小
    private int                               poolSize        = Runtime.getRuntime().availableProcessors() * 10;

    // executor 列表
    private List<ExecutorService>             executorServices = new ArrayList<ExecutorService>();

    //executor 个数，最大同时分表的个数；
    private int                               executorSize    = 8;

    // 多数据源
    private IGhostDataSource                 ghostDataSource;

    public void setMoodleDataSource(IGhostDataSource ghostDataSource) {
        this.ghostDataSource = ghostDataSource;
    }

    private final Log                      logger                   = LogFactory.getLog(getClass());

    private Resource                       configLocation;

    private Resource[]                     mapperLocations;

    private GhostSqlSessionFactoryBuilder sqlSessionFactoryBuilder = new GhostSqlSessionFactoryBuilder();

    private SqlSessionFactory              sqlSessionFactory;

    private Properties                     configurationProperties;

    private String                         environment              = SqlSessionFactoryBean.class.getSimpleName();

    private boolean                        failFast;

    private Interceptor[]                  plugins;

    private TypeHandler[]                  typeHandlers;

    private String                         typeHandlersPackage;

    private Class<?>[]                     typeAliases;

    private String                         typeAliasesPackage;
    
    public int getPoolSize() {
        return poolSize;
    }

    
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    
    public int getExecutorSize() {
        return executorSize;
    }

    
    public void setExecutorSize(int executorSize) {
        this.executorSize = executorSize;
    }

    public List<ExecutorService> getExecutorServices() {
        return executorServices;
    }


    /**
     * Mybatis plugin list.
     * 
     * @since 1.0.1
     * @param plugins list of plugins
     */
    public void setPlugins(Interceptor[] plugins) {
        this.plugins = plugins;
    }

    /**
     * Packages to search for type aliases.
     * 
     * @since 1.0.1
     * @param typeAliasesPackage package to scan for domain objects
     */
    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    /**
     * Packages to search for type handlers.
     * 
     * @since 1.0.1
     * @param typeHandlersPackage package to scan for type handlers
     */
    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    /**
     * Set type handlers. They must be annotated with {@code MappedTypes} and optionally with {@code MappedJdbcTypes}
     * 
     * @since 1.0.1
     * @param typeHandlers Type handler list
     */
    public void setTypeHandlers(TypeHandler[] typeHandlers) {
        this.typeHandlers = typeHandlers;
    }

    /**
     * List of type aliases to register. They can be annotated with {@code Alias}
     * 
     * @since 1.0.1
     * @param typeAliases Type aliases list
     */
    public void setTypeAliases(Class<?>[] typeAliases) {
        this.typeAliases = typeAliases;
    }

    /**
     * If true, a final check is done on Configuration to assure that all mapped statements are fully loaded and there
     * is no one still pending to resolve includes. Defaults to false.
     * 
     * @since 1.0.1
     * @param failFast enable failFast
     */
    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    /**
     * Set the location of the MyBatis {@code SqlSessionFactory} config file. A typical value is
     * "WEB-INF/mybatis-configuration.xml".
     */
    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Set locations of MyBatis mapper files that are going to be merged into the {@code SqlSessionFactory}
     * configuration at runtime. This is an alternative to specifying "&lt;sqlmapper&gt;" entries in an MyBatis config
     * file. This property being based on Spring's resource abstraction also allows for specifying resource patterns
     * here: e.g. "classpath*:sqlmap/*-mapper.xml".
     */
    public void setMapperLocations(Resource[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    /**
     * Set optional properties to be passed into the SqlSession configuration, as alternative to a {@code
     * &lt;properties&gt;} tag in the configuration xml file. This will be used to resolve placeholders in the config
     * file.
     */
    public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
        this.configurationProperties = sqlSessionFactoryProperties;
    }

    /**
     * Sets the {@code SqlSessionFactoryBuilder} to use when creating the {@code SqlSessionFactory}. This is mainly
     * meant for testing so that mock SqlSessionFactory classes can be injected. By default, {@code
     * SqlSessionFactoryBuilder} creates {@code DefaultSqlSessionFactory} instances.
     */
    public void setSqlSessionFactoryBuilder(GhostSqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
        this.sqlSessionFactoryBuilder = sqlSessionFactoryBuilder;
    }

    /**
     * <b>NOTE:</b> This class <em>overrides</em> any {@code Environment} you have set in the MyBatis config file. This
     * is used only as a placeholder name. The default value is {@code SqlSessionFactoryBean.class.getSimpleName()}.
     * 
     * @param environment the environment name
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setRouter(IGhostRouter<MyBatisRoutingFact> router) {
        this.router = router;
    }

    /**
     * {@inheritDoc}
     */
    public SqlSessionFactory getObject() throws Exception {
        if (this.sqlSessionFactory == null) {
            afterPropertiesSet();
        }

        return this.sqlSessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends SqlSessionFactory> getObjectType() {
        return this.sqlSessionFactory == null ? SqlSessionFactory.class : this.sqlSessionFactory.getClass();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void onApplicationEvent(ApplicationEvent event) {
        if (failFast && event instanceof ContextRefreshedEvent) {
            // fail-fast -> check all statements are completed
            this.sqlSessionFactory.getConfiguration().getMappedStatementNames();
        }
    }

    private Map<String, TransactionFactory> transactionFactories;

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(ghostDataSource, "Property 'ghostDatSurce' is required");
        Assert.notNull(ghostDataSource.getDataSources(), "Property 'dataSources' is required");
        Assert.notNull(sqlSessionFactoryBuilder, "Property 'sqlSessionFactoryBuilder' is required");
        Assert.notNull(router, "Property 'sqlSessionFactoryBuilder' is required");
        
        this.sqlSessionFactory = buildSqlSessionFactory();
    }

    /**
     * Build a {@code SqlSessionFactory} instance. The default implementation uses the standard MyBatis {@code
     * XMLConfigBuilder} API to build a {@code SqlSessionFactory} instance based on an Reader.
     * 
     * @return SqlSessionFactory
     * @throws IOException if loading the config file failed
     */
    protected SqlSessionFactory buildSqlSessionFactory() throws IOException {

        GhostConfiguration configuration;

        GhostXMLConfigBuilder xmlConfigBuilder = null;
        if (this.configLocation != null) {
            xmlConfigBuilder = new GhostXMLConfigBuilder(this.configLocation.getInputStream(), null, this.configurationProperties);
            configuration = (GhostConfiguration) xmlConfigBuilder.getConfiguration();
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Property 'configLocation' not specified, using default MyBatis Configuration");
            }
            configuration = new GhostConfiguration();
        }

        if (StringUtils.hasLength(this.typeAliasesPackage)) {
            String[] typeAliasPackageArray = StringUtils.tokenizeToStringArray(this.typeAliasesPackage,
                                                                               ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            for (String packageToScan : typeAliasPackageArray) {
                configuration.getTypeAliasRegistry().registerAliases(packageToScan);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Scanned package: '" + packageToScan + "' for aliases");
                }
            }
        }

        if (!ObjectUtils.isEmpty(this.typeAliases)) {
            for (Class<?> typeAlias : this.typeAliases) {
                configuration.getTypeAliasRegistry().registerAlias(typeAlias);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Registered type alias: '" + typeAlias + "'");
                }
            }
        }

        if (!ObjectUtils.isEmpty(this.plugins)) {
            for (Interceptor plugin : this.plugins) {
                configuration.addInterceptor(plugin);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Registered plugin: '" + plugin + "'");
                }
            }
        }

        if (StringUtils.hasLength(this.typeHandlersPackage)) {
            String[] typeHandlersPackageArray = StringUtils.tokenizeToStringArray(this.typeHandlersPackage,
                                                                                  ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            for (String packageToScan : typeHandlersPackageArray) {
                configuration.getTypeHandlerRegistry().register(packageToScan);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Scanned package: '" + packageToScan + "' for type handlers");
                }
            }
        }

        if (!ObjectUtils.isEmpty(this.typeHandlers)) {
            for (TypeHandler typeHandler : this.typeHandlers) {
                configuration.getTypeHandlerRegistry().register(typeHandler);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Registered type handler: '" + typeHandler + "'");
                }
            }
        }

        if (xmlConfigBuilder != null) {
            try {
                xmlConfigBuilder.parse();

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Parsed configuration file: '" + this.configLocation + "'");
                }
            } catch (Exception ex) {
                throw new NestedIOException("Failed to parse config resource: " + this.configLocation, ex);
            } finally {
                ErrorContext.instance().reset();
            }
        }

        Map<String, Environment> environments = new HashMap<String, Environment>();

        for (Map.Entry<String, DataSource> entry : ghostDataSource.getDataSources().entrySet()) {
            TransactionFactory transactionFactory = transactionFactories == null ? null : transactionFactories.get(entry.getKey());
            if (transactionFactory == null) {
                transactionFactory = new SpringManagedTransactionFactory(entry.getValue());
            }
            environments.put(entry.getKey(), new Environment(this.environment, transactionFactory, entry.getValue()));

        }
        
        //构造Executor列表
        configuration.setExecutorServices(setupDefaultExecutorServices());
        configuration.setConcurrentRequestProcessor(new DefaultConcurrentRequestProcessor());
        
        configuration.setEnvironments(environments);

        // 注入路由器
        configuration.setRouter(router);

        if (!ObjectUtils.isEmpty(this.mapperLocations)) {
            for (Resource mapperLocation : this.mapperLocations) {
                if (mapperLocation == null) {
                    continue;
                }

                // this block is a workaround for issue
                // http://code.google.com/p/mybatis/issues/detail?id=235
                // when running MyBatis 3.0.4. But not always works.
                // Not needed in 3.0.5 and above.
                String path;
                if (mapperLocation instanceof ClassPathResource) {
                    path = ((ClassPathResource) mapperLocation).getPath();
                } else {
                    path = mapperLocation.toString();
                }

                try {
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(), configuration, path,
                                                                             configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } catch (Exception e) {
                    throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                } finally {
                    ErrorContext.instance().reset();
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Parsed mapper file: '" + mapperLocation + "'");
                }
            }
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Property 'mapperLocations' was not specified or no matching resources found");
            }
        }

        return this.sqlSessionFactoryBuilder.build(configuration);
    }

    /**
     * If more than one data sources are involved in a data access request, we need a collection of executors to execute
     * the request on these data sources in parallel.<br>
     * But in case the users forget to inject a collection of executors for this purpose, we need to setup a default
     * one.<br>
     */
    private List<ExecutorService> setupDefaultExecutorServices() {
        if (CollectionUtils.isEmpty(this.getExecutorServices())) {
            for (int i= 0 ; i< this.executorSize; i++){
                ExecutorService executor = createExecutorForSpecificDataSource(this.getPoolSize(), "ExecutorServiceThread-" + i);
                this.getExecutorServices().add(executor);
            }
        }
        return getExecutorServices();
    }

    private ExecutorService createExecutorForSpecificDataSource(int poolSize, String threadName) {
        final ExecutorService executor = createCustomExecutorService(poolSize, threadName);
        //dispose executor implicitly
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                if (executor == null) {
                    return;
                }

                try {
                    executor.shutdown();
                    executor.awaitTermination(5, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    logger.error("interrupted when shuting down the query executor:\n{}", e);
                }
            }
        });
        return executor;
    }

    private ExecutorService createCustomExecutorService(int poolSize, final String threadName) {
        int coreSize = Runtime.getRuntime().availableProcessors();
        if (poolSize < coreSize) {
            coreSize = poolSize;
        }
        ThreadFactory tf = new ThreadFactory() {

            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, threadName);
                t.setDaemon(true);
                return t;
            }
        };
        BlockingQueue<Runnable> queueToUse = new LinkedBlockingQueue<Runnable>();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, poolSize, 60, TimeUnit.SECONDS, queueToUse, tf,
                                                                   new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }

}
