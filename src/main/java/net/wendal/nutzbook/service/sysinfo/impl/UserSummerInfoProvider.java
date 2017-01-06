package net.wendal.nutzbook.service.sysinfo.impl;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.util.RedisKey;

@IocBean
public class UserSummerInfoProvider extends AbstractSysInfoProvider implements RedisKey {

    public String name() {
        return "用户统计";
    }

    public String description() {
        return "统计24小时,一周,一个月的活跃用户";
    }
    
    @Aop("redis")
    public List<NutMap> fetch() {
        List<NutMap> re = new ArrayList<>();
        // 统计今天的发帖量
        NutMap map = new NutMap();
        map.put("name", "用户总量");
        map.put("value", dao.count(UserProfile.class, null));
        re.add(map);
        
        map = new NutMap();
        map.put("name", "最近一周新增用户");
        map.put("value", dao.count(User.class, Cnd.where("createTime", ">", from7d())));
        re.add(map);
        

        map = new NutMap();
        map.put("name", "最近30天新增用户");
        map.put("value", dao.count(User.class, Cnd.where("createTime", ">", from30d())));
        re.add(map);
        
        //map = new NutMap();
        //map.put("name", "今天在线人数");
        //map.put("value", jedis().bitcount(RKEY_ONLINE_DAY + Toolkit.today_yyyyMMdd()));
        //re.add(map);
        
        //map = new NutMap();
        //map.put("name", "当前小时在线人数");
        //map.put("value", jedis().bitcount(RKEY_ONLINE_HOUR + Toolkit.today_yyyyMMddHH()));
        //re.add(map);
        
        long now = System.currentTimeMillis();
        
        map = new NutMap();
        map.put("name", "最近24小时在线人数");
        map.put("value", jedis().zcount(RKEY_USER_LVTIME, now - 24*3600*1000L, now));
        re.add(map);
        
        return re;
    }

}
