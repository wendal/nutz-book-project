package net.wendal.nutzbook.yvr;

import org.beetl.ext.nutz.BeetlViewMaker;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.ViewMaker;

import net.sf.ehcache.CacheManager;
import net.wendal.nutzbook.core.service.AuthorityService;
import net.wendal.nutzbook.yvr.beetl.MarkdownFunction;
import net.wendal.nutzbook.yvr.service.YvrService;
import net.wendal.nutzbook.yvr.util.Markdowns;

public class YvrMainSetup implements Setup {
    
    private static final Log log = Logs.get();

    @Override
    public void init(NutConfig nc) {
        Ioc ioc = nc.getIoc();
        Daos.createTablesInPackage(nc.getIoc().get(Dao.class), getClass().getPackage().getName() + ".bean", false);
        // 检查一下Ehcache CacheManager 是否正常.
        CacheManager cacheManager = ioc.get(CacheManager.class);
        log.debug("Ehcache CacheManager = " + cacheManager);

        // 设置Markdown缓存
        if (cacheManager.getCache("markdown") == null)
            cacheManager.addCache("markdown");
        Markdowns.cache = cacheManager.getCache("markdown");

        ioc.get(YvrService.class).updateTopicTypeCount();
        ioc.get(AuthorityService.class).initFormPackage(getClass().getPackage().getName());
        
        for (ViewMaker vm : nc.getViewMakers()) {
            if (vm instanceof BeetlViewMaker) {
                ((BeetlViewMaker)vm).groupTemplate.registerFunction("markdown", new MarkdownFunction(ioc.get(PropertiesProxy.class, "conf")));
            }
        }
    }

    @Override
    public void destroy(NutConfig nc) {
        Markdowns.cache = null;
    }

}
