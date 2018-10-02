package net.wendal.nutzbook.sysinfo.service.impl;

import java.util.Date;

import org.nutz.dao.Dao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;

import net.wendal.nutzbook.sysinfo.service.SysInfoProvider;

public abstract class AbstractSysInfoProvider implements SysInfoProvider {
    
    @Inject
    protected Dao dao;
    
    @Inject
    protected PropertiesProxy conf;

    public String id() {
        return getClass().getName();
    }

    public long getLastModifyTime() {
        return System.currentTimeMillis();
    }

    protected Date from24h() {
        return new Date(System.currentTimeMillis() - 86400L*1000);
    }
    
    protected Date from7d() {
        return new Date(System.currentTimeMillis() - 86400L*1000*7);
    }
    
    protected Date from30d() {
        return new Date(System.currentTimeMillis() - 86400L*1000*30);
    }
}
