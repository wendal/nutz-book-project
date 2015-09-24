package net.wendal.nutzbook.module.yvr;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.service.yvr.YvrService;
import net.wendal.nutzbook.util.Markdowns;

import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;

@IocBean
@At("/yvr/api/v1")
@Ok("json")
@Fail("http:500")
public class YvrApiModule extends BaseModule {
	
	@Inject("java:$conf.getInt('topic.pageSize', 15)")
	protected int pageSize;
	
	@Inject
	protected YvrService yvrService;

	@GET
	@At
	@Aop("redis")
	public Object topics(@Param("page")int page, @Param("tab")String type, 
			@Param("limit")int limit, @Param("mdrender")String mdrender) {
		if (page < 1)
			page = 1;
		if (limit < 0 || limit > pageSize)
			limit = pageSize;
		Pager pager = dao.createPager(page, limit);
		if (type == null)
			type = "ask";
		HashMap<Integer, UserProfile> authors = new HashMap<Integer, UserProfile>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Set<String> ids = jedis().zrevrangeByScore("t:update:" + type, System.currentTimeMillis(), 0, pager.getOffset(), pager.getPageSize());
		for (String id : ids) {
			Topic topic = dao.fetch(Topic.class, id);
			if (topic == null)
				continue;
			list.add(_topic(topic, authors, mdrender));
		}
		return new NutMap().setv("data", list);
	}
	
	@Aop("redis")
	@GET
	@At("/topic/?")
	public Object topic(String id,@Param("mdrender")String mdrender) {
		Topic topic = dao.fetch(Topic.class, id);
		if (id == null) {
			return HttpStatusView.HTTP_404;
		}
		NutMap tp = _topic(topic, new HashMap<Integer, UserProfile>(), mdrender);
		
		List<NutMap> replies = new ArrayList<NutMap>();
		for (TopicReply reply : dao.query(TopicReply.class, Cnd.where("topicId", "=", id))) {
			dao.fetchLinks(reply, null);
			dao.fetchLinks(reply.getAuthor(), null);
			reply.setUps(jedis().zrange("t:like:" + reply.getId(), 0, System.currentTimeMillis()));
			
			NutMap re = new NutMap();
			re.put("id", reply.getId());
			re.put("author", _author(reply.getAuthor()));
			
			re.put("content", "false".equals(mdrender) ? reply.getContent() : Markdowns.toHtml(reply.getContent()));
			re.put("ups", new ArrayList<String>(reply.getUps()));
			re.put("create_at", _time(reply.getCreateTime()));
			replies.add(re);
		}
		
		tp.put("replies", replies);
		return tp;
	}
	
	public NutMap _topic(Topic topic, Map<Integer, UserProfile> authors, String mdrender) {
		yvrService.fillTopic(topic, authors);
		NutMap tp = new NutMap();
		tp.put("id", topic.getId());
		tp.put("author_id", ""+topic.getAuthor().getUserId());
		tp.put("tab", topic.getType().toString());
		tp.put("content", "false".equals(mdrender) ? topic.getContent() : Markdowns.toHtml(topic.getContent()));
		tp.put("title", topic.getTitle());
		if (topic.getLastComment() != null)
			tp.put("last_reply_at", _time(topic.getLastComment().getCreateTime()));
		tp.put("good", topic.isGood());
		tp.put("top", topic.isTop());
		tp.put("reply_count", topic.getReplyCount());
		tp.put("visit_count", topic.getVisitCount());
		tp.put("create_at", _time(topic.getCreateTime()));
		tp.put("author", _author(topic.getAuthor()));
		return tp;
	}
	
	public NutMap _author(UserProfile profile) {
		NutMap author = new NutMap();
		author.setv("loginname", profile.getLoginname());
		author.setv("avatar_url", Mvcs.getServletContext().getContextPath() + "/yvr/u/" + profile.getLoginname() + "/avatar");
		return author;
	}
	
	public String _time(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(date);
	}
}
