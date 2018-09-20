package net.wendal.nutzbook.beepay.module;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.Daos;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import net.wendal.nutzbook.beepay.bean.QQRobotHandlerBean;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.core.service.JvmJsService;

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
@Api(name="QQ机器人", description="QQ机器人,对接查询和请求转发")
@At("/robot")
@Filters
@IocBean(create="init")
public class QQRobotModule extends BaseModule {
    
    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected JvmJsService jvmJsService;
    
    /**
     * 命令开始符号
     */
    public static final char cmd = '#';
    /**
     * AT模板
     */
    public static final String AT_TPL = "@%s(%s)";

    // TODO 加上KEY认证
    @At("/msg")
    @Ok("raw")
    @Fail("void")
    public String msg(@Param("..") NutMap data, HttpServletRequest req)
            throws Exception {
        String groupId = data.getString("GroupId");
        if (Strings.isBlank(groupId))
            return "";
        String route = conf.get("qqbot.route."+groupId);
        if (!Strings.isBlank(route)) {
            if (!route.startsWith("http"))
                route = "https://" + route + "/robot/msg";
            log.debug("route to " + route);
            Request _req = Request.create(route, METHOD.POST, data);
            Response resp = Sender.create(_req).setTimeout(5*1000).send();
            if (resp.isOK()) {
                return resp.getContent();
            }
            log.debug("route seem not good code="+resp.getStatus());
            return "";
        }
        if (!Strings.equals(data.getString("Event"), "ClusterIM")) {
            return "";
        }
        if (!Strings.startsWithChar(data.getString("Message"), cmd)) {
            return "";
        }
        String key = data.getString("Message");
        if (key.length() == 1)
            return "";
        key = key.substring(1).trim();
        if (key.length() == 0)
            return "";
        List<QQRobotHandlerBean> handlers = dao.query(QQRobotHandlerBean.class, Cnd.where("enable", "=", true).asc("priority").asc("name"));
        for (QQRobotHandlerBean handler : handlers) {
            String resp = execHandler(handler, key);
            if (Strings.isBlank(resp))
                continue;
            if ("NOP".equals(resp))
                return "";
            return resp;
        }
        
        return "";
    }
    
    protected String execHandler(QQRobotHandlerBean handler, String key) {
        if (!Strings.isBlank(handler.getMatch()) && key.matches(handler.getMatch())) {
            return null;
        }
        if ("text".equals(handler.getCtype())) {
            return handler.getContent();
        }
        if ("js".equals(handler.getCtype())) {
            NutMap context = new NutMap();
            context.put("ioc", ioc);
            context.put("robot_req", null);
            context.put("req", Mvcs.getReq());
            context.put("arg", key);
            try {
                Object obj = jvmJsService.invoke(handler.getContent(), context, false);
                if (obj != null)
                    return obj.toString();
            }
            catch (Exception e) {
                log.debug("js eval fail", e);
            }
        }
        return null;
    }
    
    public void init() {
        dao.create(QQRobotHandlerBean.class, false);
        Daos.migration(dao, QQRobotHandlerBean.class, true, false, false);
    }
}
