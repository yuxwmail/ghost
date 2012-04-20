package org.knot.ghost.core.session;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.knot.ghost.exception.GhostException;



/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostSqlSessionManager implements SqlSessionFactory, SqlSession,
		IGhostSqlSessionFactory {

	private final GhostSqlSessionFactory sqlSessionFactory;
	private final SqlSession sqlSessionProxy;

	private ThreadLocal<SqlSession> localSqlSession = new ThreadLocal<SqlSession>();

	public static GhostSqlSessionManager newInstance(Reader reader) {
		return new GhostSqlSessionManager(new GhostSqlSessionFactoryBuilder()
				.build(reader, null, null));
	}

	public static GhostSqlSessionManager newInstance(Reader reader,
			String environment) {
		return new GhostSqlSessionManager(new GhostSqlSessionFactoryBuilder()
				.build(reader, environment, null));
	}

	public static GhostSqlSessionManager newInstance(Reader reader,
			Properties properties) {
		return new GhostSqlSessionManager(new GhostSqlSessionFactoryBuilder()
				.build(reader, null, properties));
	}

	public static GhostSqlSessionManager newInstance(InputStream inputStream) {
		return new GhostSqlSessionManager(new GhostSqlSessionFactoryBuilder()
				.build(inputStream, null, null));
	}

	public static GhostSqlSessionManager newInstance(InputStream inputStream,
			String environment) {
		return new GhostSqlSessionManager(new GhostSqlSessionFactoryBuilder()
				.build(inputStream, environment, null));
	}

	public static GhostSqlSessionManager newInstance(InputStream inputStream,
			Properties properties) {
		return new GhostSqlSessionManager(new GhostSqlSessionFactoryBuilder()
				.build(inputStream, null, properties));
	}

	public static GhostSqlSessionManager newInstance(
			GhostSqlSessionFactory sqlSessionFactory) {
		return new GhostSqlSessionManager(sqlSessionFactory);
	}

	private GhostSqlSessionManager(GhostSqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
		this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(
				SqlSessionFactory.class.getClassLoader(),
				new Class[] { SqlSession.class }, new SqlSessionInterceptor());
	}

	public void startManagedSession(Environment environment) {
		this.localSqlSession.set(openSession(environment));
	}

	public void startManagedSession(boolean autoCommit, Environment environment) {
		this.localSqlSession.set(openSession(autoCommit,environment));
	}

	public void startManagedSession(Connection connection, Environment environment) {
		this.localSqlSession.set(openSession(connection,environment));
	}

	public void startManagedSession(TransactionIsolationLevel level, Environment environment) {
		this.localSqlSession.set(openSession(level,environment));
	}

	public void startManagedSession(ExecutorType execType, Environment environment) {
		this.localSqlSession.set(openSession(execType,environment));
	}

	public void startManagedSession(ExecutorType execType, boolean autoCommit, Environment environment) {
		this.localSqlSession.set(openSession(execType, autoCommit,environment));
	}

	public void startManagedSession(ExecutorType execType,
			TransactionIsolationLevel level, Environment environment) {
		this.localSqlSession.set(openSession(execType, level,environment));
	}

	public void startManagedSession(ExecutorType execType, Connection connection, Environment environment) {
		this.localSqlSession.set(openSession(execType, connection,environment));
	}

	public boolean isManagedSessionStarted() {
		return this.localSqlSession.get() != null;
	}

	public SqlSession openSession(Environment environment) {
		return sqlSessionFactory.openSession(environment);
	}

	public SqlSession openSession(boolean autoCommit, Environment environment) {
		return sqlSessionFactory.openSession(autoCommit, environment);
	}

	public SqlSession openSession(Connection connection, Environment environment) {
		return sqlSessionFactory.openSession(connection, environment);
	}

	public SqlSession openSession(TransactionIsolationLevel level,
			Environment environment) {
		return sqlSessionFactory.openSession(level, environment);
	}

	public SqlSession openSession(ExecutorType execType, Environment environment) {
		return sqlSessionFactory.openSession(execType, environment);
	}

	public SqlSession openSession(ExecutorType execType, boolean autoCommit,
			Environment environment) {
		return sqlSessionFactory.openSession(execType, autoCommit, environment);
	}

	public SqlSession openSession(ExecutorType execType,
			TransactionIsolationLevel level, Environment environment) {
		return sqlSessionFactory.openSession(execType, level, environment);
	}

	public SqlSession openSession(ExecutorType execType, Connection connection,
			Environment environment) {
		return sqlSessionFactory.openSession(execType, connection, environment);
	}

	public Configuration getConfiguration() {
		return sqlSessionFactory.getConfiguration();
	}

	public Object selectOne(String statement) {
		return sqlSessionProxy.selectOne(statement);
	}

	public Object selectOne(String statement, Object parameter) {
		return sqlSessionProxy.selectOne(statement, parameter);
	}

	public Map selectMap(String statement, String mapKey) {
		return sqlSessionProxy.selectMap(statement, mapKey);
	}

	public Map selectMap(String statement, Object parameter, String mapKey) {
		return sqlSessionProxy.selectMap(statement, parameter, mapKey);
	}

	public Map selectMap(String statement, Object parameter, String mapKey,
			RowBounds rowBounds) {
		return sqlSessionProxy.selectMap(statement, parameter, mapKey,
				rowBounds);
	}

	public List selectList(String statement) {
		return sqlSessionProxy.selectList(statement);
	}

	public List selectList(String statement, Object parameter) {
		return sqlSessionProxy.selectList(statement, parameter);
	}

	public List selectList(String statement, Object parameter,
			RowBounds rowBounds) {
		return sqlSessionProxy.selectList(statement, parameter, rowBounds);
	}

	public void select(String statement, ResultHandler handler) {
		sqlSessionProxy.select(statement, handler);
	}

	public void select(String statement, Object parameter, ResultHandler handler) {
		sqlSessionProxy.select(statement, parameter, handler);
	}

	public void select(String statement, Object parameter, RowBounds rowBounds,
			ResultHandler handler) {
		sqlSessionProxy.select(statement, parameter, rowBounds, handler);
	}

	public int insert(String statement) {
		return sqlSessionProxy.insert(statement);
	}

	public int insert(String statement, Object parameter) {
		return sqlSessionProxy.insert(statement, parameter);
	}

	public int update(String statement) {
		return sqlSessionProxy.update(statement);
	}

	public int update(String statement, Object parameter) {
		return sqlSessionProxy.update(statement, parameter);
	}

	public int delete(String statement) {
		return sqlSessionProxy.delete(statement);
	}

	public int delete(String statement, Object parameter) {
		return sqlSessionProxy.delete(statement, parameter);
	}

	public <T> T getMapper(Class<T> type) {
		return getConfiguration().getMapper(type, this);
	}

	public Connection getConnection() {
		final SqlSession sqlSession = localSqlSession.get();
		if (sqlSession == null)
			throw new SqlSessionException(
					"Error:  Cannot get connection.  No managed session is started.");
		return sqlSession.getConnection();
	}

	public void clearCache() {
		final SqlSession sqlSession = localSqlSession.get();
		if (sqlSession == null)
			throw new SqlSessionException(
					"Error:  Cannot clear the cache.  No managed session is started.");
		sqlSession.clearCache();
	}

	public void commit() {
		final SqlSession sqlSession = localSqlSession.get();
		if (sqlSession == null)
			throw new SqlSessionException(
					"Error:  Cannot commit.  No managed session is started.");
		sqlSession.commit();
	}

	public void commit(boolean force) {
		final SqlSession sqlSession = localSqlSession.get();
		if (sqlSession == null)
			throw new SqlSessionException(
					"Error:  Cannot commit.  No managed session is started.");
		sqlSession.commit(force);
	}

	public void rollback() {
		final SqlSession sqlSession = localSqlSession.get();
		if (sqlSession == null)
			throw new SqlSessionException(
					"Error:  Cannot rollback.  No managed session is started.");
		sqlSession.rollback();
	}

	public void rollback(boolean force) {
		final SqlSession sqlSession = localSqlSession.get();
		if (sqlSession == null)
			throw new SqlSessionException(
					"Error:  Cannot rollback.  No managed session is started.");
		sqlSession.rollback(force);
	}

	public void close() {
		final SqlSession sqlSession = localSqlSession.get();
		if (sqlSession == null)
			throw new SqlSessionException(
					"Error:  Cannot close.  No managed session is started.");
		try {
			sqlSession.close();
		} finally {
			localSqlSession.set(null);
		}
	}

	private class SqlSessionInterceptor implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			final SqlSession sqlSession = GhostSqlSessionManager.this.localSqlSession
					.get();
			if (sqlSession != null) {
				try {
					return method.invoke(sqlSession, args);
				} catch (Throwable t) {
					throw ExceptionUtil.unwrapThrowable(t);
				}
			} else {

				final SqlSession autoSqlSession = openSession();
				try {
					final Object result = method.invoke(autoSqlSession, args);
					autoSqlSession.commit();
					return result;
				} catch (Throwable t) {
					autoSqlSession.rollback();
					throw ExceptionUtil.unwrapThrowable(t);
				} finally {
					autoSqlSession.close();
				}
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
