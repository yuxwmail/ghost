package org.knot.ghost.core;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.knot.ghost.core.session.GhostSqlSessionFactory;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
/**
 * TODO 类实现描述
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public final class GhostSqlSessionUtils {

    private static final Log                 logger    = LogFactory.getLog(GhostSqlSessionUtils.class);

    private static final Map<Object, Object> connectResources = new ConcurrentHashMap<Object, Object>();

    /**
     * This class can't be instantiated, exposes static utility methods only.
     */
    private GhostSqlSessionUtils(){
        // do nothing
    }

    /**
     * If a Spring transaction is active it uses {@code DataSourceUtils} to get a Spring managed {@code Connection},
     * then creates a new {@code SqlSession} with this connection and synchronizes it with the transaction. If there is
     * not an active transaction it gets a connection directly from the {@code DataSource} and creates a {@code
     * SqlSession} with it.
     * 
     * @param sessionFactory a MyBatis {@code SqlSessionFactory} to create new sessions
     * @param executorType The executor type of the SqlSession to create
     * @param exceptionTranslator Optional. Translates SqlSession.commit() exceptions to Spring exceptions.
     * @throws TransientDataAccessResourceException if a transaction is active and the {@code SqlSessionFactory} is not
     * using a {@code SpringManagedTransactionFactory}
     * @see SpringManagedTransactionFactory
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory, ExecutorType executorType,
                                           PersistenceExceptionTranslator exceptionTranslator, Environment environment, Connection conn) {

        Assert.notNull(sessionFactory, "No SqlSessionFactory specified");
        Assert.notNull(executorType, "No ExecutorType specified");
        Assert.notNull(environment, "No environment specified");

        // Object o = TransactionSynchronizationManager.getResource(environment.getDataSource());
        // SqlSessionHolder holder = null;
        // if (o instanceof SqlSessionHolder) {
        SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(environment);
        if (holder != null && holder.isSynchronizedWithTransaction()) {
            if (holder.getExecutorType() != executorType) {
                throw new TransientDataAccessResourceException("Cannot change the ExecutorType when there is an existing transaction");
            }

            holder.requested();

            if (logger.isDebugEnabled()) {
                logger.debug("Fetched SqlSession [" + holder.getSqlSession() + "] from current transaction");
            }
            return holder.getSqlSession();
        }
        
        // }
        // SqlSessionFactoryBean unwraps TransactionAwareDataSourceProxies but
        // we keep this check for the case that SqlSessionUtils is called from custom code
        // boolean transactionAware = (dataSource instanceof TransactionAwareDataSourceProxy);
        // Connection conn;
        // try {
        // conn = transactionAware ? dataSource.getConnection() : DataSourceUtils.getConnection(dataSource);
        // } catch (SQLException e) {
        // throw new CannotGetJdbcConnectionException("Could not get JDBC Connection for SqlSession", e);
        // }
        //
        // System.out.println("111111111111111:" + conn.toString());
        // if (logger.isDebugEnabled()) {
        // logger.debug("Creating SqlSession with JDBC Connection [" + conn + "]");
        // }

        // Assume either DataSourceTransactionManager or the underlying
        // connection pool already dealt with enabling auto commit.
        // This may not be a good assumption, but the overhead of checking
        // connection.getAutoCommit() again may be expensive (?) in some drivers
        // (see DataSourceTransactionManager.doBegin()). One option would be to
        // only check for auto commit if this function is being called outside
        // of DSTxMgr, but to do that we would need to be able to call
        // ConnectionHolder.isTransactionActive(), which is protected and not
        // visible to this class.
        SqlSession session = ((GhostSqlSessionFactory) sessionFactory).openSession(executorType, conn, environment);
        
        
        
        

        // Register session holder and bind it to enable synchronization.
        //
        // Note: The DataSource should be synchronized with the transaction
        // either through DataSourceTxMgr or another tx synchronization.
        // Further assume that if an exception is thrown, whatever started the transaction will
        // handle closing / rolling back the Connection associated with the SqlSession.
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            if (!(environment.getTransactionFactory() instanceof SpringManagedTransactionFactory)
                && DataSourceUtils.isConnectionTransactional(conn, environment.getDataSource())) {
                throw new TransientDataAccessResourceException(
                                                               "SqlSessionFactory must be using a SpringManagedTransactionFactory in order to use Spring transaction synchronization");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Registering transaction synchronization for SqlSession [" + session + "]");
            }
            holder = new SqlSessionHolder(session, executorType, exceptionTranslator);
            TransactionSynchronizationManager.bindResource(environment, holder);
            TransactionSynchronizationManager.registerSynchronization(new SqlSessionSynchronization(holder, environment));
            holder.setSynchronizedWithTransaction(true);
            holder.requested();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("SqlSession [" + session + "] was not registered for synchronization because synchronization is not active");
            }
        }

        return session;
    }

    /**
     * Checks if {@code SqlSession} passed as an argument is managed by Spring {@code TransactionSynchronizationManager}
     * If it is not, it closes it, otherwise it just updates the reference counter and lets Spring call the close
     * callback when the managed transaction ends
     * 
     * @param session
     * @param sessionFactory
     */
    public static void closeSqlSession(SqlSession session, Environment environment) {

        Assert.notNull(session, "No SqlSession specified");
        Assert.notNull(environment, "No SqlSessionFactory specified");

        SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(environment);
        if ((holder != null) && (holder.getSqlSession() == session)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Releasing transactional SqlSession [" + session + "]");
            }
            holder.released();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Closing no transactional SqlSession [" + session + "]");
            }
            session.close();
        }
    }

    /**
     * Returns if the {@code SqlSession} passed as an argument is being managed by Spring
     * 
     * @param session a MyBatis SqlSession to check
     * @param sessionFactory the SqlSessionFactory which the SqlSession was built with
     * @return true if session is transactional, otherwise false
     */
    public static boolean isSqlSessionTransactional(SqlSession session, Environment environment) {
        Assert.notNull(session, "No SqlSession specified");
        Assert.notNull(environment, "No SqlSessionFactory specified");

        SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(environment);

        return (holder != null) && (holder.getSqlSession() == session);
    }

    /**
     * Callback for cleaning up resources. It cleans TransactionSynchronizationManager and also commits and closes the
     * {@code SqlSession}. It assumes that {@code Connection} life cycle will be managed by {@code
     * DataSourceTransactionManager} or {@code JtaTransactionManager}
     */
    private static final class SqlSessionSynchronization extends TransactionSynchronizationAdapter {

        private final SqlSessionHolder holder;

        // private final DataSource dataSource;

        private final Environment      environment;

        public SqlSessionSynchronization(SqlSessionHolder holder, Environment environment){
            Assert.notNull(holder, "Parameter 'holder' must be not null");
            Assert.notNull(environment, "Parameter 'sessionFactory' must be not null");

            this.holder = holder;
            this.environment = environment;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getOrder() {
            // order right before any Connection synchronization
            return DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void suspend() {
            TransactionSynchronizationManager.unbindResource(this.environment);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void resume() {
            TransactionSynchronizationManager.bindResource(this.environment, this.holder);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beforeCommit(boolean readOnly) {
            // Connection commit or rollback will be handled by ConnectionSynchronization or
            // DataSourceTransactionManager.
            // But, do cleanup the SqlSession / Executor, including flushing BATCH statements so
            // they are actually executed.
            // SpringManagedTransaction will no-op the commit over the jdbc connection
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Transaction synchronization committing SqlSession [" + this.holder.getSqlSession() + "]");
                    }
                    this.holder.getSqlSession().commit();
                } catch (PersistenceException p) {
                    if (this.holder.getPersistenceExceptionTranslator() != null) {
                        throw this.holder.getPersistenceExceptionTranslator().translateExceptionIfPossible(p);
                    }
                    throw p;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void afterCompletion(int status) {
            // unbind the SqlSession from tx synchronization
            // Note, assuming DefaultSqlSession, rollback is not needed because rollback on
            // SpringManagedTransaction will no-op anyway. In addition, closing the session cleans
            // up the same internal resources as rollback.
            if (!this.holder.isOpen()) {
                TransactionSynchronizationManager.unbindResource(this.environment);
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Transaction synchronization closing SqlSession [" + this.holder.getSqlSession() + "]");
                    }
                    this.holder.getSqlSession().close();
                } finally {
                    this.holder.reset();
                }
            }
        }
    }

}
