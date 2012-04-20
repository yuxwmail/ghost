package org.knot.ghost.router.support;

 
/**
 * 
 * 路由元数据信息
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class MyBatisRoutingFact {
	// SQL identity
	private String sqlmap;
	// the argument of SQL action
	private Object argument;
	
	//读写操作
	private StatusEnum status = StatusEnum.INIT;
	
    public MyBatisRoutingFact(String sqlmap, Object arg){
		this.sqlmap   = sqlmap;
		this.argument = arg;
//		this.status = status;
	}
	
    
    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public StatusEnum getStatus() {
        return status;
    }
    
    public String getSqlmap() {
        return sqlmap;
    }
	 
	public Object getArgument() {
		return argument;
	}
	 
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sqlmap == null) ? 0 : sqlmap.hashCode());
		result = prime * result
				+ ((argument == null) ? 0 : argument.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyBatisRoutingFact other = (MyBatisRoutingFact) obj;
		if (sqlmap == null) {
			if (other.sqlmap != null)
				return false;
		} else if (!sqlmap.equals(other.sqlmap))
			return false;
		if (argument == null) {
			if (other.argument != null)
				return false;
		} else if (!argument.equals(other.argument))
			return false;
		
		if (status == null) {
            if (other.status != null)
                return false;
        } else if (status.getValue()!=other.status.getValue())
            return false;
		
		return true;
	}
	@Override
	public String toString() {
		return "MyBatisRoutingFact [action=" + sqlmap + ", argument=" + argument + ", status=" + status.getValue()
				+ "]";
	}
}
