package net.wendal.nutzbook.common.mvc;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

public class LogTimeProcessor extends AbstractProcessor {
	
	private static final Log log = Logs.get();
	protected static PropertiesProxy conf;

	@Override
	public void process(ActionContext ac) throws Throwable {
	    if (conf == null)
	        conf = ac.getIoc().get(PropertiesProxy.class, "conf");
        HttpServletRequest req = ac.getRequest();
	    req.setAttribute("conf", conf);
	    req.setAttribute("cdnbase", "");
		Stopwatch sw = Stopwatch.begin();
		try {
			doNext(ac);
		} finally {
			sw.stop();
			if (log.isDebugEnabled()) {
				log.debugf("[%-4s]URI=%s %d %sms", req.getMethod(), req.getRequestURI(), ac.getResponse().getStatus(), sw.getDuration());
			}
		}
	}

}
