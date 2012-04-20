package org.knot.ghost.core.executor.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 
 * 替换mybatis的 PreparedStatementHandler
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostPreparedStatementHandler extends GhostBaseStatementHandler {

	  public GhostPreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) {
	    super(executor, mappedStatement, parameter, rowBounds, resultHandler);
	  }

	  public int update(Statement statement)
	      throws SQLException {
	    PreparedStatement ps = (PreparedStatement) statement;
	    ps.execute();
	    int rows = ps.getUpdateCount();
	    Object parameterObject = boundSql.getParameterObject();
	    KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
	    keyGenerator.processAfter(executor, mappedStatement, ps, parameterObject);
	    return rows;
	  }

	  public void batch(Statement statement)
	      throws SQLException {
	    PreparedStatement ps = (PreparedStatement) statement;
	    ps.addBatch();
	  }

	  public List query(Statement statement, ResultHandler resultHandler)
	      throws SQLException {
	    PreparedStatement ps = (PreparedStatement) statement;
	    ps.execute();
	    return resultSetHandler.handleResultSets(ps);
	  }

	  protected Statement instantiateStatement(Connection connection) throws SQLException {
	    String sql = boundSql.getSql();
	    if (mappedStatement.getKeyGenerator() instanceof Jdbc3KeyGenerator) {
	      return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
	    } else if (mappedStatement.getResultSetType() != null) {
	      return connection.prepareStatement(sql, mappedStatement.getResultSetType().getValue(), ResultSet.CONCUR_READ_ONLY);
	    } else {
	      return connection.prepareStatement(sql);
	    }
	  }

	  public void parameterize(Statement statement)
	      throws SQLException {
	    KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
	    ErrorContext.instance().store();
	    keyGenerator.processBefore(executor, mappedStatement, statement, boundSql.getParameterObject());
	    ErrorContext.instance().recall();
	    rebindGeneratedKey();
	    parameterHandler.setParameters((PreparedStatement) statement);
	  }

	}
