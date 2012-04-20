package org.knot.ghost.support.utils;

import java.util.Map;

public abstract class MapUtils {

    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !MapUtils.isEmpty(map);
    }

}
