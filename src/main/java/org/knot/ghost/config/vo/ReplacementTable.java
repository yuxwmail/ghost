package org.knot.ghost.config.vo;

import org.apache.commons.lang.StringUtils;

/**
 * 分表元数据
 * 
 * originalTableName:原表名
 * targetTableName:替换的目标表名
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class ReplacementTable {

    //原表名
    private String originalTableName;
    
    //替换的目标表名
    private String targetTableName;

    public String getOriginalTableName() {
        return originalTableName;
    }

    public void setOriginalTableName(String originalTableName) {
        this.originalTableName = StringUtils.trimToEmpty(originalTableName);
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = StringUtils.trimToEmpty(targetTableName);
    }

}
