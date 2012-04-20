package org.knot.ghost.core.spring.mapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostMapperScannerConfigurer implements BeanFactoryPostProcessor, InitializingBean, ApplicationContextAware{
	
    private String basePackage;

    private boolean addToConfig = true;

    private SqlSessionFactory sqlSessionFactory;

    private SqlSessionTemplate sqlSessionTemplate;

    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;
    
    private ApplicationContext applicationContext;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMarkerInterface(Class<?> superClass) {
        this.markerInterface = superClass;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.basePackage, "Property 'basePackage' is required");
    }

    /**
     * {@inheritDoc}
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    	MoodleScanner scanner = new MoodleScanner((BeanDefinitionRegistry) beanFactory);
        scanner.setResourceLoader(this.applicationContext);

        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    private final class MoodleScanner extends ClassPathBeanDefinitionScanner {

        public MoodleScanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        /**
         * Configures parent scanner to search for the right interfaces. It can search for all
         * interfaces or just for those that extends a markerInterface or/and those annotated with
         * the annotationClass
         */
        @Override
        protected void registerDefaultFilters() {
            boolean acceptAllInterfaces = true;

            // if specified, use the given annotation and / or marker interface
            if (GhostMapperScannerConfigurer.this.annotationClass != null) {
                addIncludeFilter(new AnnotationTypeFilter(GhostMapperScannerConfigurer.this.annotationClass));
                acceptAllInterfaces = false;
            }

            // override AssignableTypeFilter to ignore matches on the actual marker interface
            if (GhostMapperScannerConfigurer.this.markerInterface != null) {
                addIncludeFilter(new AssignableTypeFilter(GhostMapperScannerConfigurer.this.markerInterface) {
                    @Override
                    protected boolean matchClassName(String className) {
                        return false;
                    }
                });
                acceptAllInterfaces = false;
            }

            if (acceptAllInterfaces) {
                // default include filter that accepts all classes
                addIncludeFilter(new TypeFilter() {
                    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
                            throws IOException {
                        return true;
                    }
                });
            }

            // always exclude interfaces with no methods
            addExcludeFilter(new TypeFilter() {
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
                        throws IOException {
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    Class<?> candidateClass = null;

                    try {
                        candidateClass = getClass().getClassLoader().loadClass(classMetadata.getClassName());
                    } catch (ClassNotFoundException ex) {
                        return false;
                    }

                    if (candidateClass.getMethods().length == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        /**
         * Calls the parent search that will search and register all the candidates. Then the
         * registered objects are post processed to set them as MapperFactoryBeans
         */
        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

            if (beanDefinitions.isEmpty()) {
                logger.warn("No MyBatis mapper was found in '" + GhostMapperScannerConfigurer.this.basePackage
                        + "' package. Please check your configuration.");
            } else {
                for (BeanDefinitionHolder holder : beanDefinitions) {
                    GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Creating MoodleMapperFactoryBean with name '" + holder.getBeanName() + "' and '"
                                + definition.getBeanClassName() + "' mapperInterface");
                    }

                    // the mapper interface is the original class of the bean
                    // but, the actual class of the bean is MoodleMapperFactoryBean
                    definition.getPropertyValues().add("mapperInterface", definition.getBeanClassName());
                    definition.setBeanClass(GhostMapperFactoryBean.class);

                    definition.getPropertyValues().add("addToConfig", GhostMapperScannerConfigurer.this.addToConfig);

                    if (GhostMapperScannerConfigurer.this.sqlSessionFactory != null) {
                        definition.getPropertyValues().add("sqlSessionFactory",
                                GhostMapperScannerConfigurer.this.sqlSessionFactory);
                    }

                    if (GhostMapperScannerConfigurer.this.sqlSessionTemplate != null) {
                        definition.getPropertyValues().add("sqlSessionTemplate",
                                GhostMapperScannerConfigurer.this.sqlSessionTemplate);
                    }
                }
            }

            return beanDefinitions;
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
        }

        @Override
        protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
            if (super.checkCandidate(beanName, beanDefinition)) {
                return true;
            } else {
                logger.warn("Skipping MoodleMapperFactoryBean with name '" + beanName + "' and '"
                        + beanDefinition.getBeanClassName() + "' mapperInterface"
                        + ". Bean already defined with the same name!");
                return false;
            }
        }
    }


}
