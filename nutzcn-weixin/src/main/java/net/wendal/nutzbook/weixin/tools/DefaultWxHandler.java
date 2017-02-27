package net.wendal.nutzbook.weixin.tools;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.weixin.impl.BasicWxHandler;

@IocBean(create="init")
public class DefaultWxHandler extends BasicWxHandler {

    @Inject
    protected PropertiesProxy conf;
    
    public void init() {
        configure(conf, "weixin.");
    }
    
    
}
