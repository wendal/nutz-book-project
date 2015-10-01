package net.wendal.nutzbook.quartz.job;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.io.ByteArrayInputStream;
import java.net.URL;

import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicType;
import net.wendal.nutzbook.service.yvr.YvrService;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.nutz.http.Http;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.nutz.lang.Xmls;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 读取 http://sexy.faceks.com/ 的福利图,生成一个帖子
 * @author wendal
 *
 */
@IocBean
public class SexyPicChecker implements Job {
	
	@Inject
	protected YvrService yvrService;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		String sitemap_str = Http.get("http://sexy.faceks.com/sitemap.xml").getContent();
		Document document = Xmls.xml(new ByteArrayInputStream(sitemap_str.getBytes()));
		NodeList list = document.getElementsByTagName("loc");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node instanceof Element) {
				String url = node.getTextContent();
				if (!url.startsWith("http://sexy.faceks.com/post/")) {
					continue;
				}
				checkURL(url);
				return;
			}
		}
	}

	@Aop("redis")
	public void checkURL(String url) {
		String id = url.substring("http://sexy.faceks.com/post/".length());
		String t = jedis().hget("t:sexy", id);
		if (t != null)
			return;
		try {
			// 读取里面的大图URL
			org.jsoup.nodes.Document doc = Jsoup.parse(new URL(url), 15000);
			Elements eles = doc.select(".pic");
			
			String title = doc.select("meta[name=\"Description\"]").first().attr("content");
			
			Topic topic = new Topic();
			topic.setType(TopicType.nb);
			topic.setTitle("[每日福利"+Times.sD(Times.now())+"]" + title);
			
			StringBuilder sb = new StringBuilder();
			sb.append("采集于 " + url + "\n\n");
			for (org.jsoup.nodes.Element ele : eles) {
				String bigimgsrc = ele.select("a").first().attr("bigimgsrc");
				sb.append("![Sexy](" + bigimgsrc+")\n");
			}
			
			sb.append("\n 采集时间: " + Times.sDTms(Times.now()));
			
			topic.setUserId(1);
			topic.setContent(sb.toString());
			
			yvrService.add(topic, 1);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
