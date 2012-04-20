package org.knot.ghost.core.spring.support;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.knot.ghost.core.spring.GhostSqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

/**
 * 
 * TODO 类实现描述 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public    abstract class GhostSqlSessionDaoSupport extends DaoSupport{
		private SqlSession sqlSession;

	    private boolean externalSqlSession;

	    @Autowired(required = false)
	    public final void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
	        if (!this.externalSqlSession) {
	            this.sqlSession = new GhostSqlSessionTemplate(sqlSessionFactory);
	        }
	    }

	    @Autowired(required = false)
	    public final void setSqlSessionTemplate(GhostSqlSessionTemplate sqlSessionTemplate) {
	        this.sqlSession = sqlSessionTemplate;
	        this.externalSqlSession = true;
	    }

	    /**
	     * Users should use this method to get a SqlSession to call its statement methods
	     * This is SqlSession is managed by spring. Users should not commit/rollback/close it
	     * because it will be automatically done.
	     * 
	     * @return Spring managed thread safe SqlSession 
	     */
	    public final SqlSession getSqlSession() {
	        return this.sqlSession;
	    }

	    /**
	     * {@inheritDoc}
	     */
	    protected void checkDaoConfig() {
	        Assert.notNull(this.sqlSession, "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");
	    }
}
