package org.knot.ghost.core.spring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.ibatis.exceptions.PersistenceException;
import org.knot.ghost.core.GhostConfiguration;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class GhostBatisExceptionTranslator implements
		PersistenceExceptionTranslator {

	private final DataSource[] dataSources;

	private SQLExceptionTranslator exceptionTranslator;

	/**
	 * Creates a new {@code DataAccessExceptionTranslator} instance.
	 * 
	 * @param dataSource
	 *            DataSource to use to find metadata and establish which error
	 *            codes are usable.
	 * @param exceptionTranslatorLazyInit
	 *            if true, the translator instantiates internal stuff only the
	 *            first time will have the need to translate exceptions.
	 */
	public GhostBatisExceptionTranslator(GhostConfiguration  conf) {
	
		List<DataSource> dss = new ArrayList<DataSource>();
    	
		GhostConfiguration  ghostConf = (GhostConfiguration)conf;
		Set<String> setEnvironment = ghostConf.getEnvironments().keySet();
		for(String key : setEnvironment){
			dss.add(ghostConf.getEnvironments().get(key).getDataSource());
		}
    	
		this.dataSources =  dss.toArray(new DataSource[0]);
		
//		if (!exceptionTranslatorLazyInit) {
//			this.initExceptionTranslator();
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DataAccessException translateExceptionIfPossible(RuntimeException e) {
		if (e instanceof PersistenceException) {
			// Batch exceptions come inside another PersistenceException
			// recursion has a risk of infinite loop so better make another if
			if (e.getCause() instanceof PersistenceException) {
				e = (PersistenceException) e.getCause();
			}
			if (e.getCause() instanceof SQLException) {
				this.initExceptionTranslator();
				return this.exceptionTranslator.translate(
						e.getMessage() + "\n", null, (SQLException) e
								.getCause());
			}
			return new MyBatisSystemException(e);
		} else {
			return null;
		}
	}

	/**
	 * Initializes the internal translator reference.
	 */
	private synchronized void initExceptionTranslator() {
		if (this.exceptionTranslator == null) {
			this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(
					this.dataSources[0]);
		}
	}
}
