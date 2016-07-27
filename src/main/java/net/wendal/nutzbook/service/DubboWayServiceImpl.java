package net.wendal.nutzbook.service;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

@IocBean(name="dubboWayService")
public class DubboWayServiceImpl implements DubboWayService {

    @Override
    @Aop("redis")
    public String redisSet(String key, String value) {
        if (Strings.isBlank(key) || Strings.isBlank(value)) {
            return "err: key/value is emtry";
        }
        if (key.length() > 64)
            return "err: key too long";
        if (value.length() > 64)
            return "err: value too long";
        jedis().setex("dubboway:"+key, 15, value);
        return "ok";
    }

    @Aop("redis")
    public String redisGet(String key) {
        if (Strings.isBlank(key))
            return "err: key is emtry";
        if (key.length() > 64)
            return "err: key too long";
        return jedis().get("dubboway:"+key);
    }
    
    public String hi(String name) {
        if ("god".equals(name))
            return "oh, my god";
        return "hi,"+name;
    }
}
