package org.nutz.mvc;

public interface ActionInterceptor {

	View intercept(ActionContext ctx);
}
