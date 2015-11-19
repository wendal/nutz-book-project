package net.wendal.nutzbook.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/vm")
public class VelocityModule {

	@Fail("http:500")
	@Ok("vm:index.vm")
	@At
	public Context index() {
		return Lang.context().set("name", "wendal");
	}
}
