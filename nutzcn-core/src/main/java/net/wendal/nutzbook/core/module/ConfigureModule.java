package net.wendal.nutzbook.core.module;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.core.service.ConfigureService;

@IocBean(create="init")
@At("/admin/config")
@Ok("json")
public class ConfigureModule extends BaseModule implements PubSub {

    @Inject
    protected ConfigureService configureService;
    
    @Inject
    protected PubSubService pubSubService;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected PropertiesProxy conf;

    @RequiresPermissions("configure:set")
    @POST
    @At
    public Object save(@Param("..") Map<String, Object> props, @Param("notify")String notify) {
        props.remove("notify");
        // 逐个更新
        for (Entry<String, Object> en : props.entrySet()) {
            String key = en.getKey();
            configureService.update(key, (String) en.getValue(), false);
        }
        configureService.doReload();
        pubSubService.fire("event:config:change", Json.toJson(props, JsonFormat.compact().setQuoteName(true).setIgnoreNull(false)));
        return ajaxOk("");
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
    
    public void init() {
        pubSubService.reg("event:config:change", this);
    }

    @Override
    public void onMessage(String channel, String message) {
    }

}
