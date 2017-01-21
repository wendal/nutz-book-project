package net.wendal.nutzbook.ig;

import java.util.List;

import org.nutz.integration.jedis.JedisAgent;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import redis.clients.jedis.Jedis;

@IocBean
public class RedisIdGenerator implements IdGenerator {
    
    @Inject
    protected JedisAgent jedisAgent;
    
    public RedisIdGenerator() {}

    public RedisIdGenerator(JedisAgent jedisAgent) {
        this.jedisAgent = jedisAgent;
    }

    public long next(String key) {
        try (Jedis jedis = jedisAgent.getResource()) {
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
