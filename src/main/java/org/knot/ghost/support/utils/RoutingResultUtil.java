package org.knot.ghost.support.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knot.ghost.config.vo.ReplacementTable;


/**
 * 
 * 表路由ThreadLocal Cache
 *  
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class RoutingResultUtil {

    private static ThreadLocal<Map<Object,List<ReplacementTable>>> threadLocal = new ThreadLocal<Map<Object,List<ReplacementTable>>>();

    public static void addReplacementTable(Object key, List<ReplacementTable> value) {
        Map<Object,List<ReplacementTable>> mapRoutingResult = null;
        mapRoutingResult = threadLocal.get();
        if (mapRoutingResult == null) {
            mapRoutingResult = new HashMap<Object,List<ReplacementTable>>();
        }
        mapRoutingResult.put(key, value);
        threadLocal.set(mapRoutingResult);
    }
    
    public static List<ReplacementTable> getAndRemoveReplacementTable(Object key) {
        Map<Object,List<ReplacementTable>> mapRoutingResult = null;
        mapRoutingResult = threadLocal.get();
        if (mapRoutingResult == null) {
            return null;
        }
//        List<ReplacementTable>  listReplacementTable =  mapRoutingResult.get(key);
       return  mapRoutingResult.remove(key);
//        return listReplacementTable;
    }

    public static void release() {
        threadLocal.remove();
    }
}
