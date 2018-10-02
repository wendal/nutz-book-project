package net.wendal.nutzbook.core.module;

import java.util.Collections;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.slog.bean.SlogBean;
import org.nutz.plugins.slog.service.SlogService;

@IocBean
@At("/admin/slog")
@Ok("json:full")
public class SlogModule extends BaseModule {
    
    @Inject
    protected SlogService slogService;

    @GET
    @RequiresRoles("admin")
    @At
    public Object list(@Param("..")Pager pager, @Param("ym")String key) {
        QueryResult qr = new QueryResult();
        qr.setPager(pager);
        Dao dao = null;
        if (Strings.isBlank(key)) 
            dao = slogService.dao();
        else
            dao = Daos.ext(this.dao, key);
        if (dao.exists(SlogBean.class)) {
            qr.setList(dao.query(SlogBean.class, Cnd.orderBy().desc("createTime"), pager));
            pager.setRecordCount(dao.count(SlogBean.class));
        } else {
            qr.setList(Collections.EMPTY_LIST);
        }
        return ajaxOk(qr);
    }
}
