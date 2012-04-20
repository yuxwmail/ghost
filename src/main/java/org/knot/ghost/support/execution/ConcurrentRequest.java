package org.knot.ghost.support.execution;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.knot.ghost.config.vo.ReplacementTable;
import org.springframework.dao.support.PersistenceExceptionTranslator;


public class ConcurrentRequest {

    private Object[]            arg;
    private Method            method;
    private ExecutorService   executor;

    private SqlSessionFactory sqlSessionFactory;

    private ExecutorType      executorType;

    private Environment       environment;
    
    private ReplacementTable rtable;  

    
    public ReplacementTable getRtable() {
        return rtable;
    }

    
    public void setRtable(ReplacementTable rtable) {
        this.rtable = rtable;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public ExecutorType getExecutorType() {
        return executorType;
    }

    private PersistenceExceptionTranslator exceptionTranslator;

    public PersistenceExceptionTranslator getExceptionTranslator() {
        return exceptionTranslator;
    }

    public void setExceptionTranslator(PersistenceExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    public Object[] getArg() {
        return arg;
    }

    public void setArg(Object[] arg) {
        this.arg = arg;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void setExecutorType(ExecutorType executorType) {
        this.executorType = executorType;
    }
}
