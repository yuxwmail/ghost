package org.knot.ghost.core.session;

import java.sql.Connection;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.TransactionIsolationLevel;

/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public interface IGhostSqlSessionFactory {

    SqlSession openSession(Environment environment);

    SqlSession openSession(boolean autoCommit, Environment environment);

    SqlSession openSession(Connection connection, Environment environment);

    SqlSession openSession(TransactionIsolationLevel level, Environment environment);

    SqlSession openSession(ExecutorType execType, Environment environment);

    SqlSession openSession(ExecutorType execType, boolean autoCommit, Environment environment);

    SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level, Environment environment);

    SqlSession openSession(ExecutorType execType, Connection connection, Environment environment);

}
