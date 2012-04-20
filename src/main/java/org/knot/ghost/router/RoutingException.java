package org.knot.ghost.router;

import org.springframework.core.NestedRuntimeException;

public class RoutingException extends NestedRuntimeException {
	private static final long serialVersionUID = 8980219652872668164L;

	public RoutingException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public RoutingException(String msg)
	{
		super(msg);
	}

}
