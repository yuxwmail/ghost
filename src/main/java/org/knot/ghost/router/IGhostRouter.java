package org.knot.ghost.router;

import java.util.List;
import java.util.Map;

import org.knot.ghost.config.vo.ReplacementTable;
import org.knot.ghost.router.rule.IRoutingRule;
import org.knot.ghost.router.support.MyBatisRoutingFact;
import org.knot.ghost.router.support.RoutingResult;


/**
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public interface IGhostRouter<T> {
    
	RoutingResult doRoute(T routingFact) throws RoutingException;
	
	public void setRuleSequences(List<IRoutingRule<MyBatisRoutingFact, Map<String, List<ReplacementTable>>>> ruleSequences);
	 
}
