package net.wendal.nutzbook.core.module;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

@Api(name="页面模块", description="拦截几个首页请求", match=ApiMatchMode.NONE)
@IocBean
public class PageModule {

	@Inject 
	protected PropertiesProxy conf;
	
	@At(value={"/", "/index"})
	@Ok("->:${obj}")
	public String index() {
	    return conf.get("website.homepage", "/adminlte");
	}
	
	@GET
	@At(value={"/user/login"})
    @Ok("->:${obj}")
    public String login() {
        return conf.get("website.loginpage", "/adminlte/user/login");
    }
}
