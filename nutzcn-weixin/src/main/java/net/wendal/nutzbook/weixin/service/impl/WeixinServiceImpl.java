package net.wendal.nutzbook.weixin.service.impl;

import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.weixin.impl.BasicWxHandler;
import org.nutz.weixin.impl.WxApi2Impl;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxHandler;

import net.wendal.nutzbook.common.util.OnConfigureChange;
import net.wendal.nutzbook.weixin.service.WeixinService;

@IocBean(name="weixinService", create="init")
public class WeixinServiceImpl implements WeixinService, OnConfigureChange {
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    protected WxHandler wxHandler;
    
    protected WxApi2 wxApi;

    @Override
    public WxApi2 getWxApi() {
        return wxApi;
    }

    @Override
    public WxHandler getHandler() {
        return wxHandler;
    }

    @Override
    public void configureChanged(Map<String, Object> props) {
        for (String key : props.keySet()) {
            if (key.startsWith("weixin.")) {
                init();
                return;
            }
        }
    }
    
    public void init() {
        String handler = conf.get("weixin.handler", "defaultWxHandler");
        // TODO 要是配错了,就不能启动了? try-catch一下?
        wxHandler = ioc.get(BasicWxHandler.class, handler).configure(conf, "weixin.");
        wxApi = new WxApi2Impl().configure(conf, "weixin.");
    }
}
