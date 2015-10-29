package org.nutz.log.impl;

import org.slf4j.spi.LocationAwareLogger;

public class Slf4jLogger extends AbstractLog {
	
	LocationAwareLogger logger;
	
	String fqcn;
	
	static final Object[] EMTRY = new Object[0];
	
	public Slf4jLogger(LocationAwareLogger logger, String fqcn) {
		this.logger = logger;
		this.fqcn = fqcn;
	}

	public void fatal(Object message, Throwable t) {
		logger.log(null, fqcn, LocationAwareLogger.ERROR_INT, (String) message, EMTRY , t);
	}

	public void error(Object message, Throwable t) {
		logger.log(null, fqcn, LocationAwareLogger.ERROR_INT, (String) message, EMTRY , t);
	}

	public void warn(Object message, Throwable t) {
		logger.log(null, fqcn, LocationAwareLogger.WARN_INT, (String) message, EMTRY , t);
	}

	public void info(Object message, Throwable t) {
		logger.log(null, fqcn, LocationAwareLogger.INFO_INT, (String) message, EMTRY , t);
	}

	public void debug(Object message, Throwable t) {
		logger.log(null, fqcn, LocationAwareLogger.DEBUG_INT, (String) message, EMTRY , t);
	}

	public void trace(Object message, Throwable t) {
		logger.log(null, fqcn, LocationAwareLogger.TRACE_INT, (String) message, EMTRY , t);
	}

	protected void log(int level, Object message, Throwable tx) {
		if (level == 50)
			level = 40;// slf4j没有FATEL level
		logger.log(null, fqcn, level, (String) message, EMTRY , tx);
	}


    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }
}
