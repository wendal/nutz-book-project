package net.wendal.nutzbook.ig;

import java.util.List;

import org.nutz.integration.jedis.JedisProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import redis.clients.jedis.Jedis;

@IocBean
public class RedisIdGenerator implements IdGenerator {
    
    @Inject
    protected JedisProxy jedisProxy;
    
    public RedisIdGenerator() {}

    public RedisIdGenerator(JedisProxy jedisProxy) {
        this.jedisProxy = jedisProxy;
    }

    public long next(String key) {
        try (Jedis jedis = jedisProxy.getResource()) {
            return jedis.incr("ig:"+key);
        }
    }

    public Object run(List<Object> fetchParam) {
        return next((String)fetchParam.get(0));
    }

    public String fetchSelf() {
        return "ig";
    }
    
}
