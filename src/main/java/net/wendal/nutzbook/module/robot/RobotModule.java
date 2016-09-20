package net.wendal.nutzbook.module.robot;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
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
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.service.yvr.LuceneSearchResult;
import net.wendal.nutzbook.service.yvr.TopicSearchService;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project msg-web
 *
 * @file RobotModule.java
 *
 * @description 机器人模块
 *
 * @time 2016年3月8日 上午10:51:26
 *
 */
@Api(name="QQ机器人", description="QQ机器人,对接查询和请求转发")
@At("/robot")
@Filters
@IocBean
public class RobotModule extends BaseModule {
    
    private static final Log log = Logs.get();

    @Inject
    protected TopicSearchService topicSearchService;

    @Inject
    protected PropertiesProxy conf;
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
            throws IOException, ParseException {
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
        // String groupId = data.getString("GroupId");
        // if (!checkGroupId(groupId)) {
        // return "";
        // }
        String key = data.getString("Message");
        if (key.length() == 1)
            return "";
        key = key.substring(1).trim();
        if (key.length() == 0)
            return "";
        if (key.equals("签到"))
            return "";

        // String at = String.format(AT_TPL, data.getString("SenderName"),
        // data.getString("Sender"));
        String at = "";

        List<LuceneSearchResult> results = topicSearchService.search(key, 3);
        if (results == null || results.size() == 0) {
            return at + " 发帖问问吧 https://" + req.getHeader("Host") + "/yvr/add";
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

    @Deprecated
    public boolean checkGroupId(String groupId) {
        if (groupId == null)
            return false;
        try {
            groupId = groupId.trim();
            String[] ids = conf.get("qqbot.groups").split(",");
            return Arrays.asList(ids).contains(groupId);
        }
        catch (Exception e) {
            return false;
        }
    }
}
