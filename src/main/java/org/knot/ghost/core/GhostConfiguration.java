package org.knot.ghost.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.knot.ghost.core.executor.statement.GhostRoutingStatementHandler;
import org.knot.ghost.router.IGhostRouter;
import org.knot.ghost.router.support.MyBatisRoutingFact;
import org.knot.ghost.support.execution.IConcurrentRequestProcessor;

/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostConfiguration extends Configuration {

    public Map<String, Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(Map<String, Environment> environments) {
        this.environments = environments;
    }

    private Map<String, Environment>          environments;

    private IGhostRouter<MyBatisRoutingFact> router;

    public IGhostRouter<MyBatisRoutingFact> getRouter() {
        return router;
    }

    public void setRouter(IGhostRouter<MyBatisRoutingFact> router) {
        this.router = router;
    }
    
    //ExecutorService 列表，用于并行执行数据库操作；
    private List<ExecutorService>             executorServices;
    
    //处理并发请求
    private IConcurrentRequestProcessor          concurrentRequestProcessor;

    
    public List<ExecutorService> getExecutorServices() {
        return executorServices;
    }

    
    public IConcurrentRequestProcessor getConcurrentRequestProcessor() {
        return concurrentRequestProcessor;
    }

    
    public void setConcurrentRequestProcessor(IConcurrentRequestProcessor concurrentRequestProcessor) {
        this.concurrentRequestProcessor = concurrentRequestProcessor;
    }

    
    public void setExecutorServices(List<ExecutorService> executorServices) {
        this.executorServices = executorServices;
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject,
                                                RowBounds rowBounds, ResultHandler resultHandler) {

        StatementHandler statementHandler = new GhostRoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds,
                                                                              resultHandler);
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }

}
