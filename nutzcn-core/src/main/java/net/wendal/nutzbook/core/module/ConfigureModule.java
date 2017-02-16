package net.wendal.nutzbook.core.module;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.core.service.ConfigureService;

@IocBean
@At("/admin/config")
@Ok("json")
public class ConfigureModule extends BaseModule {
    
    @Inject
    protected ConfigureService configureService;
    
    @Inject
    protected PropertiesProxy conf;

    @RequiresPermissions("configure:set")
    @POST
    @At
    public Object save(@Param("..")Map<String, Object> props) {
        // 当前,这个接口仅允许修改website相关的属性
        for (Entry<String, Object> en : props.entrySet()) {
            String key = en.getKey();
            if (key.startsWith("website.")) {
                configureService.update(key, (String)en.getValue(), false);
                continue;
            }
        }
        configureService.doReload();
        return ajaxOk(null);
    }
    
    @RequiresPermissions("configure:query")
    @At
    public Object list(@Param("prefix")String prefix) {
        if (Strings.isBlank(prefix)) {
            prefix = "website";
        }
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("admin") || subject.isPermitted("configure:"+prefix)) {
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
        } else {
            return ajaxFail("无权限访问");
        }
    }
    
}
