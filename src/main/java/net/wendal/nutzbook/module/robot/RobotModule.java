package net.wendal.nutzbook.module.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.queryparser.classic.ParseException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

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
@At("robot")
@Filters
@IocBean
public class RobotModule extends BaseModule {

	@Inject
	protected TopicSearchService topicSearchService;
	
	@Inject
	protected PropertiesProxy conf;
	/**
	 * 命令开始符号
	 */
	public static final char cmd = '#';

	// TODO 加上KEY认证
	@At("/msg")
	@Ok("raw")
	public String msg(@Param("..") NutMap data, HttpServletRequest req) throws IOException, ParseException {
	    if (!Strings.equals(data.getString("Event"), "ClusterIM")) {
	        return "";
	    }
	    if (!Strings.startsWithChar(data.getString("Message"), cmd)) {
	        return "";
	    }
	    String groupId = data.getString("GroupId");
	    if (!checkGroupId(groupId)) {
	        return "";
	    }
	    String key = data.getString("Message");
	    if (key.length() == 1)
	        return "";
	    key = key.substring(1).trim();
	    if (key.length() == 0)
	        return "";
	    List<LuceneSearchResult> results = topicSearchService.search(key, true, 3);
	    if (results == null || results.size() == 0) {
            return "没有相关帖子,要不发帖问问? http://" + req.getHeader("Host") + "/yvr/add";
        }
	    final StringBuilder msgbBuilder = new StringBuilder("检索结果:\r\n");
        for (LuceneSearchResult result : results) {
            Topic topic = dao.fetch(Topic.class, result.getId());
            if (topic == null)
                continue;
            topic.setTitle(result.getResult());
            String text = String.format("%s http://%s/yvr/t/%s\r\n", topic.getTitle(), req.getHeader("Host"), topic.getId());
            msgbBuilder.append(text);
        }
        return msgbBuilder.toString();
	}

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
