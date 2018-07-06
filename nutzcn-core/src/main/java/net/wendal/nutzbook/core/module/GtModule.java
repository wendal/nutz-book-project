package net.wendal.nutzbook.core.module;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.service.GtService;

@IocBean
@At("/gt")
@Ok("json:full")
public class GtModule {

    @Inject
    protected GtService gtService;
    
    
    @RequiresAuthentication
    @At
    public NutMap captcha(HttpServletRequest req) {
        return gtService.captcha(Toolkit.uid()+"", Lang.getIP(req));
    }
}
