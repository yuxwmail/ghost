package org.knot.ghost.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.Validate;
import org.knot.ghost.datasources.IGhostDataSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;



public class MultipleDataSourcesTransactionManager extends AbstractGHostPlatformTransactionManager
        implements InitializingBean {
    protected transient Logger               logger              = org.slf4j.LoggerFactory
                                                                         .getLogger(MultipleDataSourcesTransactionManager.class);

    private static final long                serialVersionUID    = 4712923770419532385L;

    private IGhostDataSource          ghostDataSource;
    private List<PlatformTransactionManager> transactionManagers = new ArrayList<PlatformTransactionManager>();

    @Override
    protected Object doGetTransaction() throws TransactionException {
        return new ArrayList<DefaultTransactionStatus>();
    }

    /**
     * We need to disable transaction synchronization so that the shared
     * transaction synchronization state will not collide with each other. BUT,
     * for LOB creators to use, we have to pay attention here:
     * <ul>
     * <li>if the LOB creator use standard preparedStatement methods, this
     * transaction synchronization setting is OK;</li>
     * <li>if the LOB creator don't use standard PS methods, you have to find
     * other way to make sure the resources your LOB creator used should be
     * cleaned up after the transaction.</li>
     * </ul>
     */
    @Override
    protected void doBegin(Object transactionObject, TransactionDefinition transactionDefinition)
            throws TransactionException {
        @SuppressWarnings("unchecked")
        List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) transactionObject;
        for (PlatformTransactionManager transactionManager : transactionManagers) {
            DefaultTransactionStatus element = (DefaultTransactionStatus) transactionManager
                    .getTransaction(transactionDefinition);
            list.add(0, element);
        }
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        @SuppressWarnings("unchecked")
        List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) status
                .getTransaction();

        logger.info("prepare to commit transactions on multiple data sources.");
        int i = 0;
        for (PlatformTransactionManager transactionManager : getTransactionManagers()) {
            TransactionStatus local = list.get(i++);
            try {
                transactionManager.commit(local);
            } catch (TransactionException e) {
                logger.error("Error in commit", e);
                // Rollback will ensue as long as rollbackOnCommitFailure=true
                throw e;
            }
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        @SuppressWarnings("unchecked")
        List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) status
                .getTransaction();

        logger.info("prepare to rollback transactions on multiple data sources.");
        int i = 0;
        TransactionException lastException = null;
        for (PlatformTransactionManager transactionManager : getTransactionManagers()) {
            TransactionStatus local = list.get(i++);
            try {
                transactionManager.rollback(local);
            } catch (TransactionException e) {
                // Log exception and try to complete rollback 
                lastException = e;
                logger.error("error occured when rolling back the transaction. \n{}", e);
            }
        }
        if (lastException != null) {
            throw lastException;
        }
    }

    
    public IGhostDataSource getMoodleDataSource() {
        return ghostDataSource;
    }

    
    public void setMoodleDataSource(IGhostDataSource ghostDataSource) {
        this.ghostDataSource = ghostDataSource;
    }

    public void afterPropertiesSet() throws Exception {
        Validate.notNull(ghostDataSource);
        for (DataSource dataSource : getMoodleDataSource().getDataSources().values()) {
            DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
            getTransactionManagers().add(txManager);
        }
        Collections.reverse(getTransactionManagers());
    }

    public List<PlatformTransactionManager> getTransactionManagers() {
        return transactionManagers;
    }

}
