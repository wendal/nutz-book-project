package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInterceptor;
import org.nutz.mvc.View;

public class ActionInterceptorProcessor extends AbstractProcessor {

	public void process(ActionContext ac) throws Throwable {
		if (ac.getModule() instanceof ActionInterceptor) {
			View view = ((ActionInterceptor)ac.getModule()).intercept(ac);
			if (view != null) {
				view.render(ac.getRequest(), ac.getResponse(), null);
				return;
			}
		}
		doNext(ac);
	}

}
