package net.wendal.nutzbook.module.yvr;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import net.wendal.nutzbook.bean.CResult;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.bean.yvr.TopicType;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.mvc.AccessTokenFilter;
import net.wendal.nutzbook.service.yvr.YvrService;
import net.wendal.nutzbook.util.Markdowns;

import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;

@IocBean(create="init")
@At("/yvr/api/v1")
@Ok("json")
@Fail("http:500")
public class YvrApiModule extends BaseModule {
	
	@Inject("java:$conf.getInt('topic.pageSize', 15)")
	protected int pageSize;
	
	@Inject
	protected YvrService yvrService;

	/**
	 * 分页获取帖子列表
	 * @param page 页数,默认为1
	 * @param type 参数名叫tab,默认是ask,如果传all,也会变成ask
	 * @param limit 每页数量
	 * @param mdrender 是否渲染md
	 * @return
	 */
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
		else if ("all".equals(type))
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
		return _map("data", list);
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
		for (TopicReply reply : dao.query(TopicReply.class, Cnd.where("topicId", "=", id).desc("createTime"))) {
			dao.fetchLinks(reply, null);
			dao.fetchLinks(reply.getAuthor(), null);
			reply.setUps(jedis().zrange("t:like:" + reply.getId(), 0, System.currentTimeMillis()));
			
			NutMap re = new NutMap();
			re.put("id", reply.getId());
			re.put("author", _author(reply.getAuthor()));
			
			re.put("content", "false".equals(mdrender) ? reply.getContent() : Markdowns.toHtml(reply.getContent(), urlbase));
			re.put("ups", new ArrayList<String>(reply.getUps()));
			re.put("create_at", _time(reply.getCreateTime()));
			replies.add(re);
		}
		
		tp.put("replies", replies);
		return _map("data", tp);
	}
	
	//@POST
	@At("/accesstoken")
	@Aop("redis")
	public Object checkAccessToken(@Param("accesstoken")String accesstoken) {
		if (Strings.isBlank(accesstoken) || accesstoken.length() > 128)
			return HTTP_403;
		String uname = jedis().hget("u:accesstoken2", accesstoken.toLowerCase());
		if (uname == null)
			return HTTP_403;
		return _map("success", true, "loginname", uname, "id", jedis().hget("u:accesstoken3", accesstoken.toLowerCase()));
	}
	
	//
	
	@At("/user/?")
	@GET
	public Object user(String loginname) {
		User user = dao.fetch(User.class, loginname);
		if (user == null)
			return HTTP_404;
		
		return _map("data", _map("loginname", loginname, "score", 0, 
				"avatar_url", _avatar_url(loginname), 
				"recent_topics", Collections.EMPTY_LIST,
				"create_at", _time(user.getCreateTime())));
	}
	
	@POST
	@At("/topics")
	@AdaptBy(type=JsonAdaptor.class)
	@Filters(@By(type=AccessTokenFilter.class))
	public Object add(@Param("..")Topic topic, @Attr(scope=Scope.SESSION, value="me")int userId, @Param("tab")String tab) {
		if (tab != null)
			topic.setType(TopicType.valueOf(tab));
		CResult re = yvrService.add(topic, userId);
		if (re.isOk()) {
			return _map("success", true, "topic_id", re.as(String.class));
		} else {
			return _map("success", false, "message", re.getMsg());
		}
	}
	
	@POST
	@At("/topic/?/replies")
	@AdaptBy(type=JsonAdaptor.class)
	@Filters(@By(type=AccessTokenFilter.class))
	public Object addReply(String topicId, @Param("..") TopicReply reply, @Attr(scope = Scope.SESSION, value = "me") int userId) {
		CResult re =  yvrService.addReply(topicId, reply, userId);
		if (re.isOk()) {
			return _map("success", true, "reply_id", re.as(String.class));
		} else {
			return _map("success", false, "message", re.getMsg());
		}
	}
	
	@POST
	@At("/reply/?/ups")
	@Filters(@By(type=AccessTokenFilter.class))
	public Object replyUp(String replyId, @Attr(scope = Scope.SESSION, value = "me") int userId) {
		CResult re =  yvrService.replyUp(replyId, userId);
		if (re.isOk()) {
			return _map("success", true, "action", re.as(String.class));
		} else {
			return _map("success", false, "message", re.getMsg());
		}
	}
	
	@GET
	@At("/message/count")
	public Object msgCount() {
		return _map("data", 0);
	}

	@GET
	@At("/messages")
	public Object getMessages() {
		return _map("data", _map("has_read_messages", Collections.EMPTY_LIST, "hasnot_read_messages", Collections.EMPTY_LIST));
	}
	
	@POST
	@At("/message/mark_all")
	public Object markAllMessage() {
		return _map("success", true);
	}
	
	// --------------------
	// 辅助方法
	
	public String _avatar_url(String loginname) {
		return urlbase + "/yvr/u/" + loginname + "/avatar";
	}
	
	public NutMap _author(UserProfile profile) {
		NutMap author = new NutMap();
		author.setv("loginname", profile.getLoginname());
		author.setv("avatar_url", _avatar_url(profile.getLoginname()));
		return author;
	}
	
	public String _time(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(date);
	}

	public NutMap _topic(Topic topic, Map<Integer, UserProfile> authors, String mdrender) {
		yvrService.fillTopic(topic, authors);
		NutMap tp = new NutMap();
		tp.put("id", topic.getId());
		tp.put("author_id", ""+topic.getAuthor().getUserId());
		tp.put("tab", topic.getType().toString());
		tp.put("content", "false".equals(mdrender) ? topic.getContent() : Markdowns.toHtml(topic.getContent(), urlbase));
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
}
