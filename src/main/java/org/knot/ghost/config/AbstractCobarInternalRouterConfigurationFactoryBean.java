package org.knot.ghost.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knot.ghost.config.vo.ReplacementTable;
import org.knot.ghost.router.DefaultGhostRouter;
import org.knot.ghost.router.IGhostRouter;
import org.knot.ghost.router.rule.IRoutingRule;
import org.knot.ghost.router.support.MyBatisRoutingFact;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * 抽象路由配置工厂类
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public abstract class AbstractCobarInternalRouterConfigurationFactoryBean implements FactoryBean, InitializingBean {

    private IGhostRouter<MyBatisRoutingFact> router;

    private boolean                           enableCache;

    private int                               cacheSize;

    private Resource                          configLocation;

    private Map<String, Object>               functionsMap                          = new HashMap<String, Object>();
    
    public static final String     DEFAULT_DATASOURCE_IDENTITY_SEPARATOR = ",";

    //多表的分割符号；
    private String                            actionPatternSeparator = DEFAULT_DATASOURCE_IDENTITY_SEPARATOR;

    public Object getObject() throws Exception {
        return this.router;
    }

    @SuppressWarnings("unchecked")
    public Class getObjectType() {
        return IGhostRouter.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        if (enableCache) {
            if (cacheSize <= 0) {
                setCacheSize(10000);
            }
        }

        this.router = new DefaultGhostRouter(enableCache, cacheSize);
        if (null != actionPatternSeparator && "".equalsIgnoreCase(actionPatternSeparator)) {
            ((DefaultGhostRouter) router).setActionPatternSeparator(actionPatternSeparator);
        }

        List<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> ruleSequences = new ArrayList<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>>();

        if (getConfigLocation() != null) {
            assembleRulesForRouter(this.router, getConfigLocation(), ruleSequences);
        }

        router.setRuleSequences(ruleSequences);
    }

    /**
     * Subclass just needs to read in rule configurations and assemble the router with the rules read from
     * configurations.
     * 
     * @param router
     * @param namespaceRules
     * @param namespaceShardingRules
     * @param sqlActionRules
     * @param sqlActionShardingRules
     */
    protected abstract void assembleRulesForRouter(IGhostRouter<MyBatisRoutingFact> router, Resource configLocation,
                                                   List<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> ruleSequences)
                                                                                                                                             throws IOException;

    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    public Resource getConfigLocation() {
        return configLocation;
    }

    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setFunctionsMap(Map<String, Object> functionMaps) {
        if (functionMaps == null) {
            return;
        }
        this.functionsMap = functionMaps;
    }

    public Map<String, Object> getFunctionsMap() {
        return functionsMap;
    }

    public String getActionPatternSeparator() {
        return actionPatternSeparator;
    }

    public void setActionPatternSeparator(String actionPatternSeparator) {
        this.actionPatternSeparator = actionPatternSeparator;
    }
}
