package net.wendal.nutzbook.module;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

import net.wendal.nutzbook.service.SysConfigureService;

@Api(name="统计分析模块", description="聚合各种数据", match=ApiMatchMode.NONE)
@IocBean
@At("/analysis")
@Ok("json")
public class AnalysisModule extends BaseModule {
    
    @Inject
    protected SysConfigureService sysConfigureService;
	
	@At(value="/sysconf/reload", top=true)
	public void sysconfReload() {
	    sysConfigureService.doReload();
	}
}
