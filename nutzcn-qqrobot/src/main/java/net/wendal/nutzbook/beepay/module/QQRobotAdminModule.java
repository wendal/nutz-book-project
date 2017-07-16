package net.wendal.nutzbook.beepay.module;

import java.util.Date;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.beepay.bean.QQRobotHandlerBean;
import net.wendal.nutzbook.core.module.BaseModule;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 * @project msg-web
 *
 * @file YvrRobotModule.java
 *
 * @description 机器人模块
 *
 * @time 2016年3月8日 上午10:51:26
 *
 */
@At("/qqrobot/admin")
@Filters
@Ok("json:full")
@IocBean(create="init")
public class QQRobotAdminModule extends BaseModule {
    
    @Inject
    protected PropertiesProxy conf;
    
    @POST
    @At
    public NutMap add(@Param("..")QQRobotHandlerBean handler) {
        if (handler.getName() != null && dao.count(QQRobotHandlerBean.class, Cnd.where("name", "=", handler.getName())) == 0) {
            return _map("ok", true, "data", dao.insert(handler));
        }
        return _map("ok", false, "msg", "重名了"); // 重名了
    }

    @POST
    @At
    public NutMap delete(@Param("..")QQRobotHandlerBean handler) {
        if (handler != null && dao.delete(handler) > 0) {
            return _map("ok", true);
        }
        return _map("ok", false, "msg", "删除失败");
    }

    @POST
    @At
    public NutMap update(@Param("..")QQRobotHandlerBean handler) {
        if (handler == null) {
            return null;
        }
        handler.setCreateTime(null);
        handler.setUpdateTime(new Date());
        if (dao.updateIgnoreNull(handler) > 0) {
            return _map("ok", true);
        }
        return _map("ok", false, "msg", "更新失败");
    }

    @RequiresAuthentication
    @RequiresRoles("admin")
    @At
    public NutMap query(@Param("..")Pager pager) {
        if (pager == null)
            pager = new Pager(1, 20);
        else if (pager.getPageNumber() < 1)
            pager.setPageNumber(1);
        QueryResult qr = new QueryResult();
        qr.setList(dao.query(QQRobotHandlerBean.class, Cnd.orderBy().asc("priority").asc("id")));
        qr.setPager(pager);
        pager.setRecordCount(dao.count(QQRobotHandlerBean.class));
        return _map("ok", true, "data", qr);
    }
}
