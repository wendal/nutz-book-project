package net.wendal.nutzbook.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

public class RedisCacheManager implements CacheManager {

    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return (Cache<K, V>) new RedisCache<>().setName(name);
    }

}
