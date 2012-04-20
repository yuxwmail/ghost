package org.knot.ghost.support.execution;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.knot.ghost.config.vo.ReplacementTable;
import org.knot.ghost.core.GhostSqlSessionUtils;
import org.knot.ghost.support.utils.CollectionUtils;
import org.knot.ghost.support.utils.RoutingResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class DefaultConcurrentRequestProcessor implements IConcurrentRequestProcessor {

    private transient final Logger logger = LoggerFactory.getLogger(DefaultConcurrentRequestProcessor.class);

    public DefaultConcurrentRequestProcessor(){
    }

    public List<Object> process(List<ConcurrentRequest> requests) {
        List<Object> resultList = new ArrayList<Object>();

        if (CollectionUtils.isEmpty(requests)) return resultList;

        List<RequestDepository> requestsDepo = fetchConnectionsAndDepositForLaterUse(requests);
        final CountDownLatch latch = new CountDownLatch(requestsDepo.size());
        List<Future<Object>> futures = new ArrayList<Future<Object>>();
        try {

            for (final RequestDepository rdepo : requestsDepo) {
                final ConcurrentRequest request = rdepo.getOriginalRequest();

                futures.add(request.getExecutor().submit(new Callable<Object>() {

                    public Object call() throws Exception {
                        try {
                            return executeWith(request.getSqlSessionFactory(), request.getMethod(), request.getArg(),
                                               request.getExecutorType(), request.getEnvironment(), request.getExceptionTranslator(),
                                               rdepo.getConnectionToUse(), request.getRtable());
                        } finally {
                            latch.countDown();
                        }
                    }
                }));
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new ConcurrencyFailureException("interrupted when processing data access request in concurrency", e);
            }

        } finally {
            for (RequestDepository depo : requestsDepo) {
                Connection springCon = depo.getConnectionToUse();
                DataSource dataSource = depo.getOriginalRequest().getEnvironment().getDataSource();
                try {
                    if (springCon != null) {
                        if (depo.isTransactionAware()) {
                            springCon.close();
                        } else {
                            DataSourceUtils.doReleaseConnection(springCon, dataSource);
                        }
                    }
                } catch (Throwable ex) {
                    logger.info("Could not close JDBC Connection", ex);
                }
            }
        }

        fillResultListWithFutureResults(futures, resultList);

        return resultList;
    }

    protected Object executeWith(SqlSessionFactory sqlSessionFactory, Method method, Object[] args, ExecutorType executorType,
                                 Environment environment, PersistenceExceptionTranslator exceptionTranslator, Connection connection, ReplacementTable rtable) {

        Object result = null;

        final SqlSession sqlSession = GhostSqlSessionUtils.getSqlSession(sqlSessionFactory, executorType, exceptionTranslator,
                                                                           environment, connection);
        
        
        // 写入ThradLocal cache
        if (null != connection && null != rtable) {
            List<ReplacementTable> rtables =  new ArrayList<ReplacementTable>();
            rtables.add(rtable);
            RoutingResultUtil.addReplacementTable(connection,rtables);
        }
        

        try {
            result = method.invoke(sqlSession, args);
            if (!GhostSqlSessionUtils.isSqlSessionTransactional(sqlSession, environment.getDataSource())) {
                sqlSession.commit();
            }
            return result;
        } catch (Throwable t) {
            Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
            if (exceptionTranslator != null && unwrapped instanceof PersistenceException) {
                throw exceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
            } else if ( unwrapped instanceof RuntimeException ){
                throw (RuntimeException)unwrapped;
            }
        } finally {
            GhostSqlSessionUtils.closeSqlSession(sqlSession, environment.getDataSource());
        }
        return result;
    }

    private void fillResultListWithFutureResults(List<Future<Object>> futures, List<Object> resultList) {
        for (Future<Object> future : futures) {
            try {
                resultList.add(future.get());
            } catch (InterruptedException e) {
                throw new ConcurrencyFailureException("interrupted when processing data access request in concurrency", e);
            } catch (ExecutionException e) {
                throw new ConcurrencyFailureException("something goes wrong in processing", e);
            }
        }
    }

    private List<RequestDepository> fetchConnectionsAndDepositForLaterUse(List<ConcurrentRequest> requests) {
        List<RequestDepository> depos = new ArrayList<RequestDepository>();
        for (ConcurrentRequest request : requests) {
            DataSource dataSource = request.getEnvironment().getDataSource();

            Connection springCon = null;
            boolean transactionAware = (dataSource instanceof TransactionAwareDataSourceProxy);
            try {
                springCon = (transactionAware ? dataSource.getConnection() : DataSourceUtils.doGetConnection(dataSource));
            } catch (SQLException ex) {
                throw new CannotGetJdbcConnectionException("Could not get JDBC Connection for SqlSession", ex);
            }

            RequestDepository depo = new RequestDepository();
            depo.setOriginalRequest(request);
            depo.setConnectionToUse(springCon);
            depo.setTransactionAware(transactionAware);
            depos.add(depo);
        }

        return depos;
    }
}
