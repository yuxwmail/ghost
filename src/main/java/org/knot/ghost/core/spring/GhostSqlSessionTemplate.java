package org.knot.ghost.core.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.knot.ghost.config.GhostRouterXmlFactoryBean;
import org.knot.ghost.config.vo.ReplacementTable;
import org.knot.ghost.core.GhostConfiguration;
import org.knot.ghost.core.GhostSqlSessionUtils;
import org.knot.ghost.core.session.GhostSqlSessionFactory;
import org.knot.ghost.router.support.MyBatisRoutingFact;
import org.knot.ghost.router.support.RoutingResult;
import org.knot.ghost.router.support.StatusEnum;
import org.knot.ghost.support.execution.ConcurrentRequest;
import org.knot.ghost.support.utils.CollectionUtils;
import org.knot.ghost.support.utils.Predicate;
import org.knot.ghost.support.utils.RoutingResultUtil;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.util.Assert;

/**
 * TODO 类实现描述
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostSqlSessionTemplate implements SqlSession {

    private String                 actionPatternSeparator = GhostRouterXmlFactoryBean.DEFAULT_DATASOURCE_IDENTITY_SEPARATOR;

    private transient final Logger logger                 = LoggerFactory.getLogger(GhostSqlSessionTemplate.class);

    /**
     * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory} provided as an argument.
     * 
     * @param sqlSessionFactory
     */
    public GhostSqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
        this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
    }

    /**
     * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory} provided as an argument and the given
     * {@code ExecutorType} {@code ExecutorType} cannot be changed once the {@code SqlSessionTemplate} is constructed.
     * 
     * @param sqlSessionFactory
     * @param executorType
     */
    public GhostSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType){

        this(sqlSessionFactory, executorType, new GhostBatisExceptionTranslator((GhostConfiguration) sqlSessionFactory.getConfiguration()));
    }

    private final SqlSessionFactory              sqlSessionFactory;

    private final ExecutorType                   executorType;

    private final SqlSession                     sqlSessionProxy;

    private final PersistenceExceptionTranslator exceptionTranslator;

    public PersistenceExceptionTranslator getExceptionTranslator() {
        return exceptionTranslator;
    }

    /**
     * Constructs a Spring managed {@code SqlSession} with the given {@code SqlSessionFactory} and {@code ExecutorType}.
     * A custom {@code SQLExceptionTranslator} can be provided as an argument so any {@code PersistenceException} thrown
     * by MyBatis can be custom translated to a {@code RuntimeException} The {@code SQLExceptionTranslator} can also be
     * null and thus no exception translation will be done and MyBatis exceptions will be thrown
     * 
     * @param sqlSessionFactory
     * @param executorType
     * @param exceptionTranslator
     */
    public GhostSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
                                   PersistenceExceptionTranslator exceptionTranslator){

        Assert.notNull(sqlSessionFactory, "Property 'sqlSessionFactory' is required");
        Assert.notNull(executorType, "Property 'executorType' is required");

        this.sqlSessionFactory = sqlSessionFactory;
        this.executorType = executorType;
        this.exceptionTranslator = exceptionTranslator;
        this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(SqlSessionFactory.class.getClassLoader(),
                                                                   new Class[] { SqlSession.class }, new MoodleSqlSessionInterceptor());
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return this.sqlSessionFactory;
    }

    public ExecutorType getExecutorType() {
        return this.executorType;
    }

    public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
        return this.exceptionTranslator;
    }

    /**
     * {@inheritDoc}
     */
    public Object selectOne(String statement) {
        return this.sqlSessionProxy.selectOne(statement);
    }

    /**
     * {@inheritDoc}
     */
    public Object selectOne(String statement, Object parameter) {
        return this.sqlSessionProxy.selectOne(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public Map<?, ?> selectMap(String statement, String mapKey) {
        return this.sqlSessionProxy.selectMap(statement, mapKey);
    }

    /**
     * {@inheritDoc}
     */
    public Map<?, ?> selectMap(String statement, Object parameter, String mapKey) {
        return this.sqlSessionProxy.selectMap(statement, parameter, mapKey);
    }

    /**
     * {@inheritDoc}
     */
    public Map<?, ?> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectMap(statement, parameter, mapKey, rowBounds);
    }

    /**
     * {@inheritDoc}
     */
    public List<?> selectList(String statement) {
        return this.sqlSessionProxy.selectList(statement);
    }

    /**
     * {@inheritDoc}
     */
    public List<?> selectList(String statement, Object parameter) {
        return this.sqlSessionProxy.selectList(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public List<?> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectList(statement, parameter, rowBounds);
    }

    /**
     * {@inheritDoc}
     */
    public void select(String statement, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, handler);
    }

    /**
     * {@inheritDoc}
     */
    public void select(String statement, Object parameter, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, handler);
    }

    /**
     * {@inheritDoc}
     */
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, rowBounds, handler);
    }

    /**
     * {@inheritDoc}
     */
    public int insert(String statement) {
        return this.sqlSessionProxy.insert(statement);
    }

    /**
     * {@inheritDoc}
     */
    public int insert(String statement, Object parameter) {
        return this.sqlSessionProxy.insert(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public int update(String statement) {
        return this.sqlSessionProxy.update(statement);
    }

    /**
     * {@inheritDoc}
     */
    public int update(String statement, Object parameter) {
        return this.sqlSessionProxy.update(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public int delete(String statement) {
        return this.sqlSessionProxy.delete(statement);
    }

    /**
     * {@inheritDoc}
     */
    public int delete(String statement, Object parameter) {
        return this.sqlSessionProxy.delete(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    /**
     * {@inheritDoc}
     */
    public void commit() {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void commit(boolean force) {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void rollback() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void rollback(boolean force) {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        throw new UnsupportedOperationException("Manual close is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */
    public void clearCache() {
        this.sqlSessionProxy.clearCache();
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getConfiguration() {
        return this.sqlSessionFactory.getConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() {
        return this.sqlSessionProxy.getConnection();
    }

    /**
     * Proxy needed to route MyBatis method calls to the proper SqlSession got from String's Transaction Manager It also
     * unwraps exceptions thrown by {@code Method#invoke(Object, Object...)} to pass a {@code PersistenceException} to
     * the {@code PersistenceExceptionTranslator}.
     */
    private class MoodleSqlSessionInterceptor implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            

            logger.info("interceptor  method :" + method.getName());

            // 构造路由元信息
            Object argument = null;
            if (args.length == 2) {
                argument = args[1];
            }
            String sqlmap = (String) args[0];

            // System.arraycopy(args, 1, argument, 0, args.length - 1);
            StatusEnum status = StatusEnum.INIT;

            if (method.getName().startsWith("select")) {
                status = StatusEnum.READ;
            } else if (method.getName().startsWith("insert") || method.getName().startsWith("update")
                       || method.getName().startsWith("delete")) {
                status = StatusEnum.UPDATE;
            }

            logger.info("create  routingFact...");
            MyBatisRoutingFact routingFact = new MyBatisRoutingFact(sqlmap, argument);
            routingFact.setStatus(status);

            GhostSqlSessionFactory ghostSessionFactory = (GhostSqlSessionFactory) GhostSqlSessionTemplate.this.sqlSessionFactory;

            GhostConfiguration ghostConfiguration = (GhostConfiguration) ghostSessionFactory.getConfiguration();

            Map<String, Environment> mapEnvironment = ghostConfiguration.getEnvironments();
            
            // 可以在这里做多数据源的路由切换；
            logger.info("begin router...");
            RoutingResult routingResult = ghostConfiguration.getRouter().doRoute(routingFact);

            List<String> dataSourceKey = null;
            if (null != routingResult.getMapTargetDataSource()) {
                dataSourceKey = new ArrayList<String>();
                
                for (Map.Entry<String, List<ReplacementTable>> listReplacementTable : routingResult.getMapTargetDataSource().entrySet()) {
//                    for (String id : StringUtils.split(listReplacementTable.getKey(), GhostSqlSessionTemplate.this.actionPatternSeparator)) {
                        dataSourceKey.add(StringUtils.trimToEmpty(listReplacementTable.getKey()));
//                    }
                }
            } else if (null != routingResult.getListDataSource()) {
                dataSourceKey = routingResult.getListDataSource();
            }

            List<ConcurrentRequest> requests = new ArrayList<ConcurrentRequest>();

            int count = 0;
            for (String key : dataSourceKey) {

                logger.info("route to datasource :" + key);

                Environment environment = mapEnvironment.get(key);

                List<ReplacementTable> listReplacementTable = null;

                // 替换的表集合
                List<ReplacementTable> realReplacementTable = null;

                // 写入ThradLocal cache
                if (null != routingResult.getMapTargetDataSource()) {
                    listReplacementTable = routingResult.getMapTargetDataSource().get(key);
                    if (null != listReplacementTable && listReplacementTable.size() > 0) {
                        ReplacementTable replace = listReplacementTable.get(0);
                        // for (ReplacementTable replace : listReplacementTable) {
                        logger.info("Original Table Name:" + replace.getOriginalTableName());
                        logger.info("Target Table Name:" + replace.getTargetTableName());
                        realReplacementTable = new ArrayList<ReplacementTable>();
                        for (String targetName : replace.getTargetTableName().split(GhostSqlSessionTemplate.this.actionPatternSeparator)) {
                            ReplacementTable rtable = new ReplacementTable();
                            rtable.setOriginalTableName(replace.getOriginalTableName());
                            rtable.setTargetTableName(targetName);
                            realReplacementTable.add(rtable);
                        }
                        // tmpReplacementTables.add(realReplacementTable);
                        // }
                    } else {
                        logger.info("no have table  of replacement");
                    }

                }
                if (null != realReplacementTable) {
                    for (ReplacementTable rtable : realReplacementTable) {

                        ConcurrentRequest request = new ConcurrentRequest();
                        request.setMethod(method);
                        request.setArg(args);
                        request.setEnvironment(environment);
                        request.setExecutor(ghostConfiguration.getExecutorServices().get(count++));
                        request.setExceptionTranslator(GhostSqlSessionTemplate.this.exceptionTranslator);
                        request.setSqlSessionFactory(GhostSqlSessionTemplate.this.sqlSessionFactory);
                        request.setExecutorType(GhostSqlSessionTemplate.this.executorType);
                        request.setRtable(rtable);

                        requests.add(request);
                    }
                } else {
                    ConcurrentRequest request = new ConcurrentRequest();
                    request.setMethod(method);
                    request.setArg(args);
                    request.setEnvironment(environment);
                    request.setExecutor(ghostConfiguration.getExecutorServices().get(count++));
                    request.setExceptionTranslator(GhostSqlSessionTemplate.this.exceptionTranslator);
                    request.setSqlSessionFactory(GhostSqlSessionTemplate.this.sqlSessionFactory);
                    request.setExecutorType(GhostSqlSessionTemplate.this.executorType);
                    requests.add(request);
                }

            }

            // 没有命中多个表，或多个库
            if (requests.size() == 1) {
                logger.info("no have parallel run sql !");
                ConcurrentRequest request = requests.get(0);
                Connection connection = null;
                boolean transactionAware = (request.getEnvironment().getDataSource() instanceof TransactionAwareDataSourceProxy);
                try {
                    connection = (transactionAware ? request.getEnvironment().getDataSource().getConnection() : DataSourceUtils.doGetConnection(request.getEnvironment().getDataSource()));
                } catch (SQLException ex) {
                    throw new CannotGetJdbcConnectionException("Could not get JDBC Connection for SqlSession", ex);
                }

                final SqlSession sqlSession = GhostSqlSessionUtils.getSqlSession(sqlSessionFactory, executorType, exceptionTranslator,
                                                                                 request.getEnvironment(), connection);
                // 写入ThradLocal cache
                if (null != connection && null != request.getRtable()) {
                    List<ReplacementTable> rtables = new ArrayList<ReplacementTable>();
                    rtables.add(request.getRtable());
                    RoutingResultUtil.addReplacementTable(connection, rtables);
                }

                try {
                    Object result = method.invoke(sqlSession, args);
                    if (!GhostSqlSessionUtils.isSqlSessionTransactional(sqlSession, request.getEnvironment())) {
                        sqlSession.commit();
                    }
                    return result;
                } catch (Throwable t) {
                    Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
                    if (exceptionTranslator != null && unwrapped instanceof PersistenceException) {
                        throw exceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
                    } else if (unwrapped instanceof RuntimeException) {
                        throw (RuntimeException) unwrapped;
                    }
                } finally {
                    GhostSqlSessionUtils.closeSqlSession(sqlSession, request.getEnvironment());
                }

            } else {// 命中多个表、或库
                logger.info("begin parallel run sql... !");
                List<Object> results = ghostConfiguration.getConcurrentRequestProcessor().process(requests);
                logger.info("end parallel run sql!");
                logger.info("begin merge data... ");
                String value = method.getName();
                if ("selectOne".equalsIgnoreCase(value)) {
                    Collection<Object> filteredResultList = CollectionUtils.select(results, new Predicate() {

                        public boolean evaluate(Object item) {
                            return item != null;
                        }
                    });
                    if (filteredResultList.size() > 1) {
                        throw new IncorrectResultSizeDataAccessException(1);
                    }
                    if (CollectionUtils.isEmpty(filteredResultList)) {
                        return null;
                    }
                    return filteredResultList.iterator().next();
                } else if ("selectList".equalsIgnoreCase(value)) {
                    // if (MapUtils.isNotEmpty(getMergers())
                    // && getMergers().containsKey(statementName)) {
                    // IMerger<Object, Object> merger = getMergers().get(statementName);
                    // if (merger != null) {
                    // return (List) merger.merge(originalResultList);
                    // }
                    // }
                    List<Object> resultList = new ArrayList<Object>();
                    for (Object item : results) {
                        resultList.addAll((List) item);
                    }
                    return resultList;
                } else if ("selectMap".equalsIgnoreCase(value)) {
                    Map<Object, Object> resultMap = new HashMap<Object, Object>();
                    for (Object item : results) {
                        resultMap.putAll((Map<?, ?>) item);
                    }
                    return resultMap;
                } else if ("insert".equalsIgnoreCase(value) || "update".equalsIgnoreCase(value) || "delete".equalsIgnoreCase(value)) {
                    Integer rowAffacted = 0;
                    for (Object item : results) {
                        rowAffacted += (Integer) item;
                    }
                    return rowAffacted;
                }
            }
            return null;
        }

    }

}
