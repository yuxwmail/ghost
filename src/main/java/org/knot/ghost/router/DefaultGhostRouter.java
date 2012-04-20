package org.knot.ghost.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.knot.ghost.config.GhostRouterXmlFactoryBean;
import org.knot.ghost.config.vo.ReplacementTable;
import org.knot.ghost.config.vo.Rule;
import org.knot.ghost.config.vo.Rules;
import org.knot.ghost.router.rule.IRoutingRule;
import org.knot.ghost.router.support.MyBatisRoutingFact;
import org.knot.ghost.router.support.RoutingResult;
import org.knot.ghost.router.support.StatusEnum;
import org.knot.ghost.support.LRUMap;
import org.knot.ghost.support.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGhostRouter implements IGhostRouter<MyBatisRoutingFact> {

    private transient final Logger logger                 = LoggerFactory.getLogger(DefaultGhostRouter.class);

    private LRUMap                 localCache;

    private boolean                enableCache            = false;

    private String                 actionPatternSeparator = GhostRouterXmlFactoryBean.DEFAULT_DATASOURCE_IDENTITY_SEPARATOR;

    public DefaultGhostRouter(boolean enableCache){
        this(enableCache, 10000);
    }

    public DefaultGhostRouter(int cacheSize){
        this(true, cacheSize);
    }

    public DefaultGhostRouter(boolean enableCache, int cacheSize){
        this.enableCache = enableCache;
        if (this.enableCache) {
            localCache = new LRUMap(cacheSize);
        }
    }

    private List<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> ruleSequences = new ArrayList<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>>();

    public RoutingResult doRoute(MyBatisRoutingFact routingFact) throws RoutingException {
        if (enableCache) {
            synchronized (localCache) {
                if (localCache.containsKey(routingFact)) {
                    RoutingResult result = (RoutingResult) localCache.get(routingFact);
                    logger.info("return routing result:{} from cache for fact:{}", result, routingFact);
                    return result;
                }
            }
        }

        RoutingResult result = new RoutingResult();

        IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>> ruleToUse = null;

        if (!CollectionUtils.isEmpty(getRuleSequences())) {
            // for (Set<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> ruleSet :
            // getRuleSequences()) {

            ruleToUse = searchMatchedRuleAgainst(getRuleSequences(), routingFact);
            // if (ruleToUse != null) {
            // break;
            // }
            // }
        }

        if (ruleToUse != null) {
            logger.info("matched with rule:{} with fact:{}", ruleToUse, routingFact);
            result.setMapTargetDataSource(ruleToUse.mapTargetDataSource());
        } else {
            logger.info("No matched rule found for routing fact:{}", routingFact);
            result.setListDataSource(searchMatchedSqlMapAgainst(routingFact));
        }

        if (enableCache) {
            synchronized (localCache) {
                localCache.put(routingFact, result);
            }
        }

        return result;
    }

    private List<String> searchMatchedSqlMapAgainst(MyBatisRoutingFact routingFact) {

        List<Rule> listRule = Rules.Instance().getListRule();

        if (CollectionUtils.isEmpty(listRule)) {
            return null;
        }
        List<String> ids = new ArrayList<String>();

        // 匹配第一个满足条件的数据源
        for (Rule rule : listRule) {
            if (rule.getSqlmap().equalsIgnoreCase(routingFact.getSqlmap())) {

                if (routingFact.getStatus() == StatusEnum.READ && null != rule.getReadDataSourceKey()
                    && !"".equals(rule.getReadDataSourceKey())) {
                    for (String id : StringUtils.split(rule.getReadDataSourceKey(), getActionPatternSeparator())) {
                        ids.add(StringUtils.trimToEmpty(id));
                    }
                } else if (routingFact.getStatus() == StatusEnum.UPDATE && null != rule.getReadDataSourceKey()
                           && !"".equals(rule.getWriteDataSourceKey())) {
                    for (String id : StringUtils.split(rule.getWriteDataSourceKey(), getActionPatternSeparator())) {
                        ids.add(StringUtils.trimToEmpty(id));
                    }
                }
                return ids;
            }
        }

        // 缺省数据源
        if (routingFact.getStatus() == StatusEnum.READ) {
            for (String id : StringUtils.split(Rules.Instance().getDefaultReadDataSourceKey(), getActionPatternSeparator())) {
                ids.add(StringUtils.trimToEmpty(id));
            }
        } else if (routingFact.getStatus() == StatusEnum.UPDATE) {
            for (String id : StringUtils.split(Rules.Instance().getDefaultWriteDataSourceKey(), getActionPatternSeparator())) {
                ids.add(StringUtils.trimToEmpty(id));
            }
        }
        return ids;
    }

    private IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>> searchMatchedRuleAgainst(
                                                                                                           List<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> rules,
                                                                                                           MyBatisRoutingFact routingFact) {
        if (CollectionUtils.isEmpty(rules)) {
            return null;
        }
        for (IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>> rule : rules) {
            if (rule.validateExpression(routingFact)) {
                return rule;
            }
        }
        return null;
    }

    public LRUMap getLocalCache() {
        return localCache;
    }

    public synchronized void clearLocalCache() {
        this.localCache.clear();
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public void setRuleSequences(List<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> ruleSequences) {
        this.ruleSequences = ruleSequences;
    }

    public List<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> getRuleSequences() {
        return ruleSequences;
    }

    public String getActionPatternSeparator() {
        return actionPatternSeparator;
    }

    public void setActionPatternSeparator(String actionPatternSeparator) {
        Validate.notNull(actionPatternSeparator);
        this.actionPatternSeparator = actionPatternSeparator;
    }
}
