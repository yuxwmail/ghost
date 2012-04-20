package org.knot.ghost.config.vo;

import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * rule元数据信息
 * 
 *  name :唯一标识
 *  sqlmap:sql的唯一id不空；
 *  expression 表达式,表达式的参数必须是sqlmap中的参数；
 *  mapTargetDataSource:key:表达式成立的目标数据源key value:表达式成立时，替换表的信息；
 *  readDataSourceKey :表达式不成立的读库（可空）
 *  writeDataSourceKey:表达式不成立的写库（可空）
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class Rule {
    
    //唯一标识
    private String name;
    
    //sqlmap:sql的唯一id不空；
    private String sqlmap;
    
//  private String namespace;
    
    //表达式,表达式的参数必须是sqlmap中的参数；
    private String expression;
    
    //key:表达式成立的目标数据源key value:表达式成立时，替换表的信息；
    private List<ReplacementTables> listTargetDataSource;
    
    //表达式成立的目标数据源key
//  private String targetDataSourceKey;
    
    //表达式不成立的读库（可空）
    private String readDataSourceKey;
    
    //表达式不成立的写库（可空）
    private String writeDataSourceKey;
    
    //表达式成立时，替换表的信息；
//   private List<ReplacementTable> listReplacementTable;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trimToEmpty(name);
    }

    public String getSqlmap() {
        return sqlmap;
    }

    public void setSqlmap(String sqlmap) {
        this.sqlmap = StringUtils.trimToEmpty(sqlmap);
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = StringUtils.trimToEmpty(expression);
    }


    public String getReadDataSourceKey() {
        return readDataSourceKey;
    }

    public void setReadDataSourceKey(String readDataSourceKey) {
        this.readDataSourceKey = StringUtils.trimToEmpty(readDataSourceKey);
    }

    public String getWriteDataSourceKey() {
        return writeDataSourceKey;
    }

    public void setWriteDataSourceKey(String writeDataSourceKey) {
        this.writeDataSourceKey = StringUtils.trimToEmpty(writeDataSourceKey);
    }

    
    public List<ReplacementTables> getListTargetDataSource() {
        return listTargetDataSource;
    }

    
    public void setListTargetDataSource(List<ReplacementTables> listTargetDataSource) {
        this.listTargetDataSource = listTargetDataSource;
    }

}
