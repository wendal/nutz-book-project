package net.wendal.nutzbook.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import redis.clients.jedis.JedisPool;

public class RedisCacheManager implements CacheManager {
    
    public static JedisPool pool;

    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return (Cache<K, V>) new RedisCache<>().setName(name);
    }

}
