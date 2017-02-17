package net.wendal.nutzbook.dwmaster.module;

import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.dwmaster.bean.DwRecord;

/**
 * nutzam.com的下载功能
 * @author wendal
 *
 */
@At("/nutzdw")
@IocBean
@Ok("json:full")
@Fail("http:500")
public class DwMasterModule extends BaseModule {
    
    @Inject
    protected PropertiesProxy conf;
    
    @At
    public NutMap list(@Param("..")Pager pager) {
        NutMap map = new NutMap("ok", true);
        map.put("data", dao.query(DwRecord.class, Cnd.orderBy().desc("updateTime"), pager));
        return map;
    }
    
    @Ok("beetl:/yvr/nutzdw/nutzdw_index.html")
    @At("/")
    public NutMap index() {
        long userId = Toolkit.uid();
        if (userId > 0)
            return new NutMap("current_user", fetch_userprofile(userId));
        return new NutMap();
    }
}
