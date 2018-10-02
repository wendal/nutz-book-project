package net.wendal.nutzbook.core.ioc;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.IocBean;

import net.sf.ehcache.CacheManager;

@IocBean
public class CoreIocBeanFactory {

    /**
     * Ehcache的CacheManager
     */
    @IocBean
    public CacheManager getCacheManager(PropertiesProxy conf) {
        CacheManager cacheManager = CacheManager.getCacheManager(conf.get("ehcache.name", "nutzbook"));
        if (cacheManager == null) {
            cacheManager = CacheManager.getInstance();
        }
        return cacheManager;
    }
}
