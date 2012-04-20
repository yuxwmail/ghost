package org.knot.ghost.core.session;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.knot.ghost.exception.GhostException;

/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostSqlSessionFactory implements IGhostSqlSessionFactory, SqlSessionFactory{

	  private static final Log log = LogFactory.getLog(GhostSqlSessionFactory.class);

	  private final Configuration configuration;
	  private final TransactionFactory managedTransactionFactory;

	  public GhostSqlSessionFactory(Configuration configuration) {
	    this.configuration = configuration;
	    this.managedTransactionFactory = new ManagedTransactionFactory();
	  }

	  public SqlSession openSession(Environment environment) {
	    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false, environment);
	  }

	  public SqlSession openSession(boolean autoCommit,Environment environment) {
	    return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, autoCommit, environment);
	  }

	  public SqlSession openSession(ExecutorType execType,Environment environment) {
	    return openSessionFromDataSource(execType, null, false, environment);
	  }

	  public SqlSession openSession(TransactionIsolationLevel level,Environment environment) {
	    return openSessionFromDataSource(configuration.getDefaultExecutorType(), level, false, environment);
	  }

	  public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level,Environment environment) {
	    return openSessionFromDataSource(execType, level, false, environment);
	  }

	  public SqlSession openSession(ExecutorType execType, boolean autoCommit,Environment environment) {
	    return openSessionFromDataSource(execType, null, autoCommit, environment);
	  }

	  public SqlSession openSession(Connection connection,Environment environment) {
	    return openSessionFromConnection(configuration.getDefaultExecutorType(), connection, environment);
	  }

	  public SqlSession openSession(ExecutorType execType, Connection connection,Environment environment) {
	    return openSessionFromConnection(execType, connection, environment);
	  }

	  public Configuration getConfiguration() {
	    return configuration;
	  }

	  private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit, Environment environment) {
	    Connection connection = null;
	    try {
//	      final Environment environment = configuration.getEnvironment();
	      final DataSource dataSource = getDataSourceFromEnvironment(environment);
	      TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
	      connection = dataSource.getConnection();
	      if (level != null) {
	        connection.setTransactionIsolation(level.getLevel());
	      }
	      connection = wrapConnection(connection);
	      Transaction tx = transactionFactory.newTransaction(connection, autoCommit);
	      Executor executor = configuration.newExecutor(tx, execType);
	      return new DefaultSqlSession(configuration, executor, autoCommit);
	    } catch (Exception e) {
	      closeConnection(connection);
	      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
	    } finally {
	      ErrorContext.instance().reset();
	    }
	  }

	  private SqlSession openSessionFromConnection(ExecutorType execType, Connection connection,Environment environment) {
	    try {
	      boolean autoCommit;
	      try {
	        autoCommit = connection.getAutoCommit();
	      } catch (SQLException e) {
	        // Failover to true, as most poor drivers
	        // or databases won't support transactions
	        autoCommit = true;
	      }
	      
//	      connection = wrapConnection(connection); //去掉代理方便比较；
	      
//	      final Environment environment = configuration.getEnvironment();
	      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
	      Transaction tx = transactionFactory.newTransaction(connection, autoCommit);
	      Executor executor = configuration.newExecutor(tx, execType);
	      return new DefaultSqlSession(configuration, executor, autoCommit);
	    } catch (Exception e) {
	      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
	    } finally {
	      ErrorContext.instance().reset();
	    }
	  }

	  private DataSource getDataSourceFromEnvironment(Environment environment) {
	    if (environment == null || environment.getDataSource() == null) {
	      throw new SqlSessionException("Configuration does not include an environment with a DataSource, so session cannot be created unless a connection is passed in.");
	    }
	    return environment.getDataSource();
	  }

	  private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
	    if (environment == null || environment.getTransactionFactory() == null) {
	      return managedTransactionFactory;
	    }
	    return environment.getTransactionFactory();
	  }

	  private Connection wrapConnection(Connection connection) {
	    if (log.isDebugEnabled()) {
	      return ConnectionLogger.newInstance(connection);
	    } else {
	      return connection;
	    }
	  }

	  private void closeConnection(Connection connection) {
	    if (connection != null) {
	      try {
	        connection.close();
	      } catch (SQLException e1) {
	        // Intentionally ignore. Prefer previous error.
	      }
	    }
	  }

	  
	  
	  
	 
	  
	  
	  
	  
	    @Override
	    @Deprecated
	    public SqlSession openSession() {
	        throw new GhostException("please call openSession(Environment) method!");
	    }

	    @Override
	    @Deprecated
	    public SqlSession openSession(boolean autoCommit) {
	        throw new GhostException("please call openSession(autoCommit,Environment) method!");
	    }

	    @Override
	    @Deprecated
	    public SqlSession openSession(Connection connection) {
	        throw new GhostException("please call openSession(connection,Environment) method!");
	    }

	    @Override
	    @Deprecated
	    public SqlSession openSession(TransactionIsolationLevel level) {
	        throw new GhostException("please call openSession(level,Environment) method!");
	    }

	    @Override
	    @Deprecated
	    public SqlSession openSession(ExecutorType execType) {
	        throw new GhostException("please call openSession(execType,Environment) method!");
	    }

	    @Override
	    @Deprecated
	    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
	        throw new GhostException("please call openSession(execType,autoCommit,Environment) method!");
	    }

	    @Override
	    @Deprecated
	    public SqlSession openSession(ExecutorType execType,
	            TransactionIsolationLevel level) {
	        throw new GhostException("please call openSession(execType,level,Environment) method!");
	    }

	    @Override
	    @Deprecated
	    public SqlSession openSession(ExecutorType execType, Connection connection) {
	        throw new GhostException("please call openSession(execType,connection,Environment) method!");
	    }
}
