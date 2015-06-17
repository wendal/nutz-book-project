package net.wendal.nutzbook.snakerflow;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.snaker.engine.SnakerInterceptor;
import org.snaker.engine.core.Execution;

public class SnakerAssignInterceptor implements SnakerInterceptor {

	private static final Log log = Logs.get();
	
	public void intercept(Execution execution) {
		log.debug("SnakerAssignInterceptor >>> ");
	}

}
