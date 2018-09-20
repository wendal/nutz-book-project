package net.wendal.nutzbook.core.ioc;

import java.util.Arrays;
import java.util.HashSet;

import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.cache.dao.DaoCacheInterceptor;
import org.nutz.plugins.cache.dao.impl.provider.EhcacheDaoCacheProvider;

import com.alibaba.druid.pool.DruidDataSource;

import net.sf.ehcache.CacheManager;

@IocBean
public class CoreIocBeanFactory {

    /**
     * 生成配置信息
     */
    @IocBean(name="conf")
    public PropertiesProxy buildConfigure() {
        return new PropertiesProxy("custom/");
    }
    
    /**
     * 主数据源
     */
    @IocBean(create="init", depose="close")
    public DruidDataSource getDataSource(PropertiesProxy conf) {
        return conf.make(DruidDataSource.class, "db.");
    }
    
    /**
     * 生成主Dao实例
     */
    @IocBean(name="dao")
    public Dao getDao(DataSource dataSource, CacheManager cacheManager, PropertiesProxy conf) throws Throwable {
        NutDao dao = new NutDao(dataSource);
        // 设置daocache
        DaoCacheInterceptor daocache = new DaoCacheInterceptor();
        // TODO 按配置选用不同的cache提供者
        EhcacheDaoCacheProvider cacheProvider = new EhcacheDaoCacheProvider();
        cacheProvider.setCacheManager(cacheManager);
        cacheProvider.init();
        daocache.setCacheProvider(cacheProvider);
        String tableNames = conf.get("daocache.tableNames");
        if (!Strings.isBlank(tableNames)) {
            daocache.setCachedTableNames(new HashSet<>(Arrays.asList(tableNames.split(","))));
        }
        dao.setInterceptors(Arrays.asList(daocache, "log", "time"));
        return dao;
    }
    
    /**
     * Ehcache的CacheManager
     */
    @IocBean
    public CacheManager getCacheManager(PropertiesProxy conf) {
        CacheManager cacheManager = CacheManager.getCacheManager(conf.get("ehcache.name", "nutzbook"));
        if (cacheManager == null) {
            cacheManager = CacheManager.getInstance();
        }
        return cacheManager;
    }
}
