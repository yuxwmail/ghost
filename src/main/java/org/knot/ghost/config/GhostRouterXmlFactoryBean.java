package org.knot.ghost.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.knot.ghost.config.vo.ReplacementTable;
import org.knot.ghost.config.vo.ReplacementTables;
import org.knot.ghost.config.vo.Rule;
import org.knot.ghost.config.vo.Rules;
import org.knot.ghost.router.IGhostRouter;
import org.knot.ghost.router.rule.IRoutingRule;
import org.knot.ghost.router.rule.MyBatisRotingRule;
import org.knot.ghost.router.support.MyBatisRoutingFact;
import org.knot.ghost.support.utils.CollectionUtils;
import org.springframework.core.io.Resource;

import com.thoughtworks.xstream.XStream;

/**
 * 路由配置工厂类
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostRouterXmlFactoryBean extends AbstractCobarInternalRouterConfigurationFactoryBean {

    @Override
    protected void assembleRulesForRouter(IGhostRouter<MyBatisRoutingFact> router, Resource configLocation,
                                          List<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> ruleSequences)
                                                                                                                                    throws IOException {

        XStream xstream = new XStream();
        xstream.alias("rules", Rules.class);
        xstream.aliasAttribute(Rules.class, "name", "name");
        xstream.aliasAttribute(Rules.class, "defaultReadDataSourceKey", "defaultReadDataSourceKey");
        xstream.aliasAttribute(Rules.class, "defaultWriteDataSourceKey", "defaultWriteDataSourceKey");
        xstream.addImplicitCollection(Rules.class, "listRule", "rule", Rule.class);
        xstream.alias("rule", Rule.class);
        xstream.aliasAttribute(Rule.class, "name", "name");
        xstream.aliasField("targetDataSource", Rule.class, "mapTargetDataSource");
        xstream.addImplicitCollection(Rule.class, "listTargetDataSource", "targetDataSource", ReplacementTables.class);
        xstream.addImplicitCollection(ReplacementTables.class, "listReplacementTable", "replacementTable", ReplacementTable.class);
        xstream.alias("targetDataSource", ReplacementTables.class);
        xstream.aliasAttribute(ReplacementTables.class, "key", "key");
        xstream.alias("replacementTable", ReplacementTable.class);
        xstream.aliasAttribute(ReplacementTable.class, "originalTableName", "originalTableName");
        xstream.aliasAttribute(ReplacementTable.class, "targetTableName", "targetTableName");

        Rules rules = (Rules) xstream.fromXML(configLocation.getInputStream());

        List<Rule> listRule = rules.getListRule();
        if (CollectionUtils.isEmpty(listRule)) {
            return;
        }
        for (Rule rule : listRule) {
            MyBatisRotingRule rotingRule = new MyBatisRotingRule(rule);
            ruleSequences.add(rotingRule);
        }
        router.setRuleSequences(ruleSequences);
    }

}
