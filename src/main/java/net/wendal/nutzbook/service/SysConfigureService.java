package net.wendal.nutzbook.service;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import net.wendal.nutzbook.bean.SysConfigure;

@IocBean(create="init")
public class SysConfigureService {
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject
    protected Dao dao;

    @RequiresPermissions("sysconf:reload")
    public void doReload() {
        _reload();
    }
    
    protected void _reload() {
        dao.each(SysConfigure.class, null, (index, en, count) -> {
            conf.put(en.getKey(), en.getValue());
        });
    }
    
    @RequiresPermissions("sysconf:update")
    public void update(String key, String value, boolean reload) {
        dao.clear(SysConfigure.class, Cnd.where("key", "=", key));
        dao.insert(new SysConfigure(key, value));
        if (reload)
            doReload();
    }
    
    public void init() {
        _reload();
    }
}
