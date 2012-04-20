package org.knot.ghost.core.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostRoutingStatementHandler   implements StatementHandler {

	private final StatementHandler delegate;

	  public GhostRoutingStatementHandler(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) {

	    switch (ms.getStatementType()) {
	      case STATEMENT:
	        delegate = new GhostSimpleStatementHandler(executor, ms, parameter, rowBounds, resultHandler);
	        break;
	      case PREPARED:
	        delegate = new GhostPreparedStatementHandler(executor, ms, parameter, rowBounds, resultHandler);
	        break;
	      case CALLABLE:
	        delegate = new GhostCallableStatementHandler(executor, ms, parameter, rowBounds, resultHandler);
	        break;
	      default:
	        throw new ExecutorException("Unknown statement type: " + ms.getStatementType());
	    }

	  }

	  public Statement prepare(Connection connection) throws SQLException {
	    return delegate.prepare(connection);
	  }

	  public void parameterize(Statement statement) throws SQLException {
	    delegate.parameterize(statement);
	  }

	  public void batch(Statement statement) throws SQLException {
	    delegate.batch(statement);
	  }

	  public int update(Statement statement) throws SQLException {
	    return delegate.update(statement);
	  }

	  public List query(Statement statement, ResultHandler resultHandler) throws SQLException {
	    return delegate.query(statement, resultHandler);
	  }

	  public BoundSql getBoundSql() {
	    return delegate.getBoundSql();
	  }

	  public ParameterHandler getParameterHandler() {
	    return delegate.getParameterHandler();
	  }

}
