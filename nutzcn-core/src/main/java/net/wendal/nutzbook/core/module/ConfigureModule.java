package net.wendal.nutzbook.core.module;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.core.service.AppPushService;
import net.wendal.nutzbook.core.service.ConfigureService;

@IocBean
@At("/admin/config")
@Ok("json")
public class ConfigureModule extends BaseModule {

    @Inject
    protected ConfigureService configureService;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected PropertiesProxy conf;

    @RequiresPermissions("configure:set")
    @POST
    @At
    public Object save(@Param("..") Map<String, Object> props) {
        // 逐个更新
        for (Entry<String, Object> en : props.entrySet()) {
            String key = en.getKey();
            configureService.update(key, (String) en.getValue(), false);
        }
        configureService.doReload();
        // 先hack一下吧...
        if (props.keySet().contains("jpush.enable") || props.keySet().contains("xmpush.enable"))
            ioc.get(AppPushService.class).reload();
        return ajaxOk(null);
    }

    @RequiresPermissions("configure:query")
    @At
    public Object list(@Param("prefix") String prefix) {
        if (Strings.isBlank(prefix)) {
            prefix = "website";
        }
        NutMap map = new NutMap();
        for (Entry<String, String> en : conf.entrySet()) {
            String key = en.getKey();
            if (key.startsWith(prefix + ".")) {
                if ("db.password".equals(key))
                    continue;
                map.put(key, en.getValue());
            }
        }
        return ajaxOk(map);
    }

}
