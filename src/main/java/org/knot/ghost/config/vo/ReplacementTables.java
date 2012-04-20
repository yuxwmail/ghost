package org.knot.ghost.config.vo;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class ReplacementTables {
    
    private String key;

    private List<ReplacementTable> listReplacementTable;

    public List<ReplacementTable> getListReplacementTable() {
        return listReplacementTable;
    }

    public void setListReplacementTable(List<ReplacementTable> listReplacementTable) {
        this.listReplacementTable = listReplacementTable;
    }
    
    public String getKey() {
        return key;
    }

    
    public void setKey(String key) {
        this.key = StringUtils.trimToEmpty(key);
    }

}
