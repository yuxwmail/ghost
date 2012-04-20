package org.knot.ghost.support.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollectionUtils {
    @SuppressWarnings("rawtypes")
    public static Collection select(Collection inputCollection, Predicate predicate) {
        List answer = new ArrayList(inputCollection.size());
        select(inputCollection, predicate, answer);
        return answer;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void select(Collection inputCollection, Predicate predicate,
                              Collection outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (Iterator iter = inputCollection.iterator(); iter.hasNext();) {
                Object item = iter.next();
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Collection coll) {
        return (coll == null || coll.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNotEmpty(Collection coll) {
        return !CollectionUtils.isEmpty(coll);
    }
}
