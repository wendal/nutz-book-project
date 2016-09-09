package net.wendal.nutzbook.module.mp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.weixin.impl.BasicWxHandler;
import org.nutz.weixin.impl.WxApi2Impl;
import org.nutz.weixin.session.memory.MemorySessionManager;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxSessionManager;
import org.nutz.weixin.util.Wxs;

import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

@Api(name="微信", description="微信demo", match=ApiMatchMode.NONE)
@At("/api/nutzam/mp")
@IocBean(create="init")
public class WxModule extends BasicWxHandler {
    
    protected WxApi2 api;
    
    protected WxSessionManager sessionManager;
    
    @Inject
    protected PropertiesProxy conf;
    
    public void init() {
        configure(conf, "wxmp.");
        api = new WxApi2Impl().configure(conf, "wxmp.");
        sessionManager = new MemorySessionManager();
    }

    /**
     * 微信入口方法
     * @param key 识别号,可以无视
     * @param req http请求
     * @return Wxs会处理好,无视吧
     * @throws IOException 各种IO错误时抛出
     */
    @At({"/in", "/in/?"})
    @Fail("http:200")
    public View msgIn(String key, HttpServletRequest req) throws IOException {
        return Wxs.handle(this, req, key);
    }
}
