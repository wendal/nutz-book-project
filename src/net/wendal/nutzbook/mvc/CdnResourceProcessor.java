package net.wendal.nutzbook.mvc;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.processor.AbstractProcessor;

public class CdnResourceProcessor extends AbstractProcessor {

	public CdnResourceProcessor() {
		if (rsPrefix == null) {
			Ioc ioc = Mvcs.getIoc();
			PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");
			useCDN = conf.getBoolean("cdn.enable", false);
			rsPrefix = conf.get("cdn.urlbase.rs");
			if (!useCDN || Strings.isBlank(rsPrefix)) {
				rsPrefix = Mvcs.getServletContext().getContextPath() + "/rs";
			}
			Logs.get().debug("Use Resource Prefix=" + rsPrefix);
		}
	}

	protected static boolean useCDN;

	protected static String rsPrefix;

	public void process(ActionContext ac) throws Throwable {
		ac.getRequest().setAttribute("rsbase", rsPrefix);
		doNext(ac);
	}
}
