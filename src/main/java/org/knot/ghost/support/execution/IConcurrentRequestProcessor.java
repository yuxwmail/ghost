package org.knot.ghost.support.execution;

import java.util.List;

public interface IConcurrentRequestProcessor {
    List<Object> process(List<ConcurrentRequest> requests)  throws Throwable ;
}
