package net.wendal.nutzbook.beepay.module;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.nutz.dao.Cnd;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import net.wendal.nutzbook.beepay.bean.QQRobotHandlerBean;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.yvr.bean.Topic;
import net.wendal.nutzbook.yvr.service.LuceneSearchResult;
import net.wendal.nutzbook.yvr.service.TopicSearchService;

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
    protected TopicSearchService topicSearchService;

    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    /**
     * 命令开始符号
     */
    public static final char cmd = '#';
    /**
     * AT模板
     */
    public static final String AT_TPL = "@%s(%s)";
    
    private ScriptEngineManager engineManager;
    
    protected ScriptEngine jsScriptEngine;

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
        List<QQRobotHandlerBean> handlers = dao.query(QQRobotHandlerBean.class, Cnd.where("enable", "=", true).desc("priority").asc("name"));
        for (QQRobotHandlerBean handler : handlers) {
            String resp = execHandler(handler, key);
            if (Strings.isBlank(resp))
                continue;
            if ("NOP".equals(resp))
                return "";
            return resp;
        }
        
        return defaultAction(req, key);
    }
    
    protected String execHandler(QQRobotHandlerBean handler, String key) {
        if (!Strings.isBlank(handler.getMatch()) && !handler.getMatch().matches(key)) {
            return null;
        }
        if ("text".equals(handler.getCtype())) {
            return handler.getContent();
        }
        if ("js".equals(handler.getCtype())) {
            Bindings bindings = jsScriptEngine.createBindings();
            bindings.put("ioc", ioc);
            bindings.put("arg", key);
            try {
                String jsStr = "function _nutzbook_robot_js(){" + handler.getContent() + "};_nutzbook_robot_js();";
                Object obj = ((Compilable) jsScriptEngine).compile(jsStr).eval(bindings);
                if (obj != null)
                    return obj.toString();
            }
            catch (ScriptException e) {
                log.debug("js eval fail", e);
            }
        }
        return null;
    }

    protected String defaultAction(HttpServletRequest req, String key) throws IOException, ParseException {
        List<LuceneSearchResult> results = topicSearchService.search(key, 3);
        if (results == null || results.size() == 0) {
            return " 发帖问问吧 https://" + req.getHeader("Host") + "/yvr/add";
        }
        final StringBuilder msgbBuilder = new StringBuilder();
        for (LuceneSearchResult result : results) {
            Topic topic = dao.fetch(Topic.class, result.getId());
            if (topic == null)
                continue;
            topic.setTitle(result.getResult());
            String text = String.format("%s https://%s/yvr/t/%s\r\n",
                                        StringEscapeUtils.unescapeHtml(topic.getTitle()),
                                        req.getHeader("Host"),
                                        topic.getId().substring(0, 6));
            msgbBuilder.append(text);
        }
        msgbBuilder.append(String.format("完整结果: https://%s/yvr/search?q=%s", req.getHeader("Host"), URLEncoder.encode(key, Encoding.UTF8)));
        return msgbBuilder.toString();
    }
    
    public void init() {
        engineManager = new ScriptEngineManager(getClass().getClassLoader());
        jsScriptEngine = engineManager.getEngineByExtension("js");
        dao.create(QQRobotHandlerBean.class, false);
    }
}
