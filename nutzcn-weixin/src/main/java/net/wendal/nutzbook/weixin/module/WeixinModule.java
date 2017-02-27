package net.wendal.nutzbook.weixin.module;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.weixin.util.Wxs;

import net.wendal.nutzbook.weixin.service.WeixinService;

@IocBean
@At("/weixin")
public class WeixinModule {
    
    @Inject
    protected WeixinService weixinService;
    
    @At({"/msgin", "/msgin/?"})
    @Fail("http:200")
    public View msgIn(String key, HttpServletRequest req) throws IOException {
        return Wxs.handle(weixinService.getHandler(), req, key);
    }

}
