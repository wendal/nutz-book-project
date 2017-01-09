package net.wendal.nutzbook.ig;

import java.util.List;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@IocBean
public class RedisIdGenerator implements IdGenerator {
    
    @Inject
    protected JedisPool jedisPool;
    
    public RedisIdGenerator() {}

    public RedisIdGenerator(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public long next(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
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
