package org.knot.ghost.router.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.knot.ghost.config.vo.ReplacementTable;
import org.knot.ghost.config.vo.ReplacementTables;
import org.knot.ghost.config.vo.Rule;
import org.knot.ghost.router.support.MyBatisRoutingFact;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于MyBatis的路由规则，及表达式验证（mvel）；
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class MyBatisRotingRule implements IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>> {

    private transient final Logger logger      = LoggerFactory.getLogger(MyBatisRotingRule.class);

    private Map<String, Object>    functionMap = new HashMap<String, Object>();

    public MyBatisRotingRule(Rule rule){
        this.rule = rule;
    }

    private Rule rule;

    // status 0：未初始化 1：表达式命中 2：sqlmap命中
    @Override
    public boolean validateExpression(MyBatisRoutingFact routingFact) {
        Validate.notNull(routingFact);
        boolean matches = StringUtils.equals(rule.getSqlmap(), routingFact.getSqlmap());
        if (matches && null != routingFact.getArgument()) {
            try {
                Map<String, Object> vrs = new HashMap<String, Object>();
                vrs.putAll(getFunctionMap());
                vrs.put("$ROOT", routingFact.getArgument()); // add top object reference for expression
                VariableResolverFactory vrfactory = new MapVariableResolverFactory(vrs);
                if (MVEL.evalToBoolean(rule.getExpression(), routingFact.getArgument(), vrfactory)) {
                    return true;
                }
            } catch (Throwable t) {
                logger.info("failed to evaluate attribute expression:'{}' with context object:'{}'\n{}", new Object[] {
                        rule.getExpression(), routingFact.getArgument(), t });
            }
        }
        return false;
    }

    // 命中的规则
    @Override
    public synchronized Map<String, List<ReplacementTable>> mapTargetDataSource() {
        Map<String, List<ReplacementTable>> mapReplaceMent = new HashMap<String, List<ReplacementTable>>();
        for (ReplacementTables replacementTables : rule.getListTargetDataSource()) {
            mapReplaceMent.put(replacementTables.getKey(), replacementTables.getListReplacementTable());
        }
        return mapReplaceMent;
    }

    public Map<String, Object> getFunctionMap() {
        return functionMap;
    }

    public void setFunctionMap(Map<String, Object> functionMap) {
        this.functionMap = functionMap;
    }
}
