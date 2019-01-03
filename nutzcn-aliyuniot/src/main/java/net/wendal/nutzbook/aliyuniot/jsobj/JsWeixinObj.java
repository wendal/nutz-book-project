package net.wendal.nutzbook.aliyuniot.jsobj;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.weixin.spi.WxApi2;

@IocBean
public class JsWeixinObj {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    protected WxApi2 wxApi;
    
    // TODO 发通知
}
