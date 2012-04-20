package org.knot.ghost.core.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;

/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostSqlSessionFactoryBuilder {

	public GhostSqlSessionFactory build(Reader reader) {
		return build(reader, null, null);
	}

	public GhostSqlSessionFactory build(Reader reader, String environment) {
		return build(reader, environment, null);
	}

	public GhostSqlSessionFactory build(Reader reader, Properties properties) {
		return build(reader, null, properties);
	}

	public GhostSqlSessionFactory build(Reader reader, String environment,
			Properties properties) {
		try {
			XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment,
					properties);
			return build(parser.parse());
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.",
					e);
		} finally {
			ErrorContext.instance().reset();
			try {
				reader.close();
			} catch (IOException e) {
				// Intentionally ignore. Prefer previous error.
			}
		}
	}

	public GhostSqlSessionFactory build(InputStream inputStream) {
		return build(inputStream, null, null);
	}

	public GhostSqlSessionFactory build(InputStream inputStream, String environment) {
		return build(inputStream, environment, null);
	}

	public GhostSqlSessionFactory build(InputStream inputStream,
			Properties properties) {
		return build(inputStream, null, properties);
	}

	public GhostSqlSessionFactory build(InputStream inputStream, String environment,
			Properties properties) {
		try {
			XMLConfigBuilder parser = new XMLConfigBuilder(inputStream,
					environment, properties);
			return build(parser.parse());
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.",
					e);
		} finally {
			ErrorContext.instance().reset();
			try {
				inputStream.close();
			} catch (IOException e) {
				// Intentionally ignore. Prefer previous error.
			}
		}
	}

	public GhostSqlSessionFactory build(Configuration config) {
		return new GhostSqlSessionFactory(config);
	}
}
