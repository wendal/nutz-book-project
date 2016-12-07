package net.wendal.nutzbook.module;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;

import net.sf.ehcache.CacheManager;

@IocBean
@At("/admin/cache")
public class CacheModule extends BaseModule {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected CacheManager cacheManager;

    @RequiresRoles("admin")
    @At
    public void clear(String name) {
        if (Strings.isBlank(name) || name.toLowerCase().contains("session"))
            return;
        net.sf.ehcache.Cache cache = cacheManager.getCache(name);
        if (cache != null) {
            log.info("cache remove ...");
            cache.removeAll();
        }
        else
            log.info("no such cache");
    }
}
