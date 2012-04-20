 package org.knot.ghost.router.rule;


public interface IRoutingRule<F, T> {
    
    boolean validateExpression(F routingFact);
    
    T mapTargetDataSource();
    
}
