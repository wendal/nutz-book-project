package net.wendal.nutzbook.webhook.module;

import java.io.IOException;
import java.util.Map;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;

import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

import net.wendal.nutzbook.core.module.BaseModule;

@Api(name="web钩子", description="例如github更新了代码,就触发一些操作", match=ApiMatchMode.NONE)
@IocBean
@At("/webhook")
public class WebHookModule extends BaseModule {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected PropertiesProxy conf;

    @AdaptBy(type=WhaleAdaptor.class)
    @At("/?/?/?")
    public void trigger(String user, String p, String s, String token, @Param("..")Map<String, Object> params) throws IOException {
        String key = String.format("webhook.%s.%s.%s", user, p, s);
        String _token = conf.get(key+".token");
        if (_token == null) {
            log.debug("no such token for "+ key);
            return;
        }
        if (!_token.equalsIgnoreCase(token)) {
            log.debug("bad token for "+ key);
            return;
        }
        String exec = conf.get(key+".exec");
        if (!Strings.isBlank(exec)) {
            Lang.execOutput(exec, Encoding.CHARSET_UTF8);
        }
    }
}
