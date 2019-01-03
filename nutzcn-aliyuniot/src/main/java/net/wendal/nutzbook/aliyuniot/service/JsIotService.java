package net.wendal.nutzbook.aliyuniot.service;

import org.nutz.aop.interceptor.async.Async;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import net.wendal.nutzbook.aliyuniot.jsobj.JsKeyValueObject;
import net.wendal.nutzbook.aliyuniot.jsobj.JsSmsObj;
import net.wendal.nutzbook.aliyuniot.jsobj.JsWeixinObj;
import net.wendal.nutzbook.core.service.JvmJsService;

@IocBean
public class JsIotService {
    
    @Inject
    protected JvmJsService jvmJsService;
    
    @Inject
    protected JsSmsObj jsSmsObj;
    
    @Inject
    protected JsWeixinObj jsWeixinObj;

    @Async
    public void call(String jsStr, NutMap context) {
        context.put("iot_kv", new JsKeyValueObject());
        context.put("iot_sms", jsSmsObj);
        context.put("iot_weixin", jsWeixinObj);
        jvmJsService.invoke(jsStr, context, false);
    }
}
