package net.wendal.nutzbook.module.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.service.yvr.LuceneSearchResult;
import net.wendal.nutzbook.service.yvr.TopicSearchService;

import org.apache.lucene.queryparser.classic.ParseException;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

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
public class RobotModule extends BaseModule {

	@Inject
	protected TopicSearchService topicSearchService;

	@At("/msg")
	@Ok("void")
	public void test(@Param("..") NutMap data, HttpServletResponse response) throws IOException, ParseException {
		if (Strings.equals(data.getString("Event"), "ClusterIM") && data.getInt("GroupId") == 68428921 && Strings.startsWithChar(data.getString("Message"), '#')) {// 是群消息而且群号正确
			String key = data.getString("Message").substring(1);

			if (Strings.isBlank(key)) {
				response.getWriter().write("关键词呢?");
			}
			List<LuceneSearchResult> results = topicSearchService.search(key, true);
			if (results == null || results.size() == 0) {
				response.getWriter().write("没有明白你想问什么?");
			}
			List<Topic> list = new ArrayList<Topic>();
			final StringBuilder msgbBuilder = new StringBuilder("机器人自动检索结果:\r\n");
			for (LuceneSearchResult result : results) {
				Topic topic = dao.fetch(Topic.class, result.getId());
				if (topic == null)
					continue;
				topic.setTitle(result.getResult());
				msgbBuilder.append(topic.getTitle() + " : http://nutz.cn/yvr/t/" + topic.getId() + "\r\n");
				list.add(topic);
			}
			response.getWriter().write(msgbBuilder.toString());
		}
	}

}
