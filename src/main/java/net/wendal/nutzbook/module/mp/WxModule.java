package net.wendal.nutzbook.module.mp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.weixin.mvc.WxAbstractModule;
import org.nutz.weixin.spi.WxHandler;
import org.nutz.weixin.util.Wxs;

@At("/api/nutzam/mp")
@IocBean
public class WxModule extends WxAbstractModule {

    @Inject
    protected WxHandler wxHandler;

    @At({"/in", "/in/?"})
    @Fail("http:200")
    public View msgIn(String key, HttpServletRequest req) throws IOException {
        return Wxs.handle(getWxHandler(key), req, key);
    }
    
    public WxHandler getWxHandler(String key) {
        return wxHandler;
    }
}
