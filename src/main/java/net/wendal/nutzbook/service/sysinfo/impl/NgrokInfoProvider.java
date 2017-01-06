package net.wendal.nutzbook.service.sysinfo.impl;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

import net.wendal.nutzbook.util.RedisKey;

@IocBean
public class NgrokInfoProvider extends AbstractSysInfoProvider implements RedisKey {

    public String name() {
        return "Ngrok内网穿透服务";
    }

    @Override
    public String description() {
        return "Ngrok内网穿透服务的相关统计";
    }

    @Override
    @Aop("redis")
    public List<NutMap> fetch() {
        List<NutMap> re = new ArrayList<>();
        NutMap map;
        
        
        map = new NutMap();
        map.put("name", "总映射数");
        map.put("value", jedis().hlen("ngrok"));
        re.add(map);
        

        Set<String> reports = jedis().zrevrangeByScore("ngrok:report", System.currentTimeMillis(), System.currentTimeMillis()/1000 - 5*60);
        if (reports.size() > 0) {
            String report = reports.iterator().next();
            NutMap tmp = Json.fromJson(NutMap.class, report);
            
            map = new NutMap();
            map.put("name", "Http通道数");
            map.put("value", tmp.get("httpTunnelMeter.count"));
            re.add(map);
            
            map = new NutMap();
            map.put("name", "windows客户端数");
            map.put("value", tmp.get("windows"));
            re.add(map);
            
            map = new NutMap();
            map.put("name", "linux客户端数");
            map.put("value", tmp.get("linux"));
            re.add(map);
            
            map = new NutMap();
            map.put("name", "osx客户端数");
            map.put("value", tmp.get("osx"));
            re.add(map);
            
            map = new NutMap();
            map.put("name", "其他客户端数");
            map.put("value", tmp.get("other"));
            re.add(map);
            
        } else {
            map = new NutMap();
            map.put("name", "服务状态");
            map.put("value", "未启动");
            re.add(map);
        }
        
        return re;
    }

}
