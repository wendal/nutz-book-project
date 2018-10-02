package net.wendal.nutzbook.weixin.module;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.weixin.bean.WeixinUser;

@IocBean
@At("/admin/weixin/user")
public class WeixinUserAdminModule extends BaseModule {

    @RequiresPermissions("weixin:user:list")
    @GET
    @At("ok:full")
    public Object query(@Param("..")Pager pager) {
        pager.setRecordCount(dao.count(WeixinUser.class));
        return ajaxOk(new QueryResult(dao.query(WeixinUser.class, null, pager), pager));
    }
}
