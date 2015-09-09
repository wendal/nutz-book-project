package net.wendal.nutzbook.beetl;

import java.io.Writer;

import org.beetl.core.ErrorHandler;
import org.beetl.core.exception.BeetlException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class BeetlFuckErrorHandler implements ErrorHandler {
	
	private static final Log log = Logs.get();

	public void processExcption(BeetlException beeExceptionos, Writer writer) {
		log.debug("fuck", beeExceptionos);
	}

}
