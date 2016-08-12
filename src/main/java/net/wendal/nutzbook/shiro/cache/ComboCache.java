package net.wendal.nutzbook.shiro.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

public class ComboCache<K, V> implements Cache<K, V> {
    
    protected List<Cache<K, V>> list = new ArrayList<>();

    public void add(Cache<K, V> cache) {
        list.add(cache);
    }
    
    public V get(K key) throws CacheException {
        for (Cache<K, V> cache : list) {
            V v = cache.get(key);
            if (v != null)
                return v;
        }
        return null;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        V v = null;
        for (Cache<K, V> cache : list) {
            V tmp = cache.put(key, value);
            if (v == null)
                v = tmp;
        }
        return v;
    }

    @Override
    public V remove(K key) throws CacheException {
        V v = null;
        for (Cache<K, V> cache : list) {
            V tmp = cache.remove(key);
            if (v == null)
                v = tmp;
        }
        return v;
    }

    @Override
    public void clear() throws CacheException {
        for (Cache<K, V> cache : list) {
            cache.clear();
        }
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        Set<K> keys = new HashSet<>();
        for (Cache<K,V> cache : list) {
            keys.addAll(cache.keys());
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Set<V> values = new HashSet<>();
        for (Cache<K,V> cache : list) {
            values.addAll(cache.values());
        }
        return values;
    }

}
