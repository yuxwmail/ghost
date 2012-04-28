package org.knot.ghost.support.execution;

import java.sql.Connection;

import org.apache.ibatis.session.SqlSession;

public class RequestDepository {

    private ConcurrentRequest originalRequest;
    private Connection        connectionToUse;
    private boolean           transactionAware;

    private SqlSession        session;

    public SqlSession getSession() {
        return session;
    }

    public void setSession(SqlSession session) {
        this.session = session;
    }

    public ConcurrentRequest getOriginalRequest() {
        return originalRequest;
    }

    public void setOriginalRequest(ConcurrentRequest originalRequest) {
        this.originalRequest = originalRequest;
    }

    public Connection getConnectionToUse() {
        return connectionToUse;
    }

    public void setConnectionToUse(Connection connectionToUse) {
        this.connectionToUse = connectionToUse;
    }

    public boolean isTransactionAware() {
        return transactionAware;
    }

    public void setTransactionAware(boolean transactionAware) {
        this.transactionAware = transactionAware;
    }

}
