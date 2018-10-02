package net.wendal.nutzbook.core.service.impl;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import net.wendal.nutzbook.core.bean.SysConfigure;
import net.wendal.nutzbook.core.service.ConfigureService;

@IocBean(create="init", name="configureService")
public class ConfigureServiceImpl implements ConfigureService {
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject
    protected Dao dao;

    public void doReload() {
        _reload();
    }
    
    protected void _reload() {
        dao.each(SysConfigure.class, null, (index, en, count) -> {
            conf.put(en.getKey(), en.getValue());
        });
    }
    
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
