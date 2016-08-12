package net.wendal.nutzbook.shiro.cache;

import org.apache.shiro.ShiroException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;

public class ComboCacheManager implements CacheManager, Initializable, Destroyable {
    
    protected CacheManager level1;
    protected CacheManager level2;

    @Override
    public void destroy() throws Exception {
        if (level2 != null && level2 instanceof Destroyable)
            ((Destroyable)level2).destroy();
        if (level1 != null && level1 instanceof Destroyable)
            ((Destroyable)level1).destroy();
    }

    @Override
    public void init() throws ShiroException {
        if (level2 != null && level2 instanceof Initializable)
            ((Initializable)level2).init();
        if (level1 != null && level1 instanceof Initializable)
            ((Initializable)level1).init();
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        ComboCache<K, V> combo = new ComboCache<>();
        if (level1 != null)
            combo.add(level1.getCache(name));
        if (level2 != null)
            combo.add(level2.getCache(name));
        return combo;
    }

    public void setLevel1(CacheManager level1) {
        this.level1 = level1;
    }
    
    public void setLevel2(CacheManager level2) {
        this.level2 = level2;
    }
}
