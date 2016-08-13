package net.wendal.nutzbook.service.sysinfo;

import java.util.List;

import org.nutz.lang.util.NutMap;

public interface SysInfoProvider {

    String id();
    
    String name();
    
    String description();
    
    List<NutMap> fetch();
    
    long getLastModifyTime();
}
