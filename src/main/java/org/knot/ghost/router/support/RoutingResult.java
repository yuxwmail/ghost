 package org.knot.ghost.router.support;

import java.util.List;
import java.util.Map;

import org.knot.ghost.config.vo.ReplacementTable;


/**
 * 
 *  
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class RoutingResult {
    
    private Map<String,List<ReplacementTable>> mapTargetDataSource;
    
    private List<String> listDataSource;
   
//    private IMerger<?, ?> merger;
//
//    public void setMerger(IMerger<?, ?> merger) {
//        this.merger = merger;
//    }
//
//    public IMerger<?, ?> getMerger() {
//        return merger;
//    }
    
    public Map<String, List<ReplacementTable>> getMapTargetDataSource() {
        return mapTargetDataSource;
    }

    
    public void setMapTargetDataSource(Map<String, List<ReplacementTable>> mapTargetDataSource) {
        this.mapTargetDataSource = mapTargetDataSource;
    }

    
    public List<String> getListDataSource() {
        return listDataSource;
    }

    
    public void setListDataSource(List<String> listDataSource) {
        this.listDataSource = listDataSource;
    }

}
