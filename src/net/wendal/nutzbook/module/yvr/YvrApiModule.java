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

/**
 * 对外公开的HTTP API, 使用http://apidocjs.com/的进行注释生成
 * @author wendal
 */
/**
 * @apiDefine TOKEN_ERROR
 * @apiError 403 The <code>accesstoken</code> is invaild
 */
/**
 * @apiDefine TOKEN
 * @apiParam {String} accesstoken 访问凭证
 *
 */
@IocBean(create="init")
@At("/yvr/api/v1")
@Ok("json")
@Fail("http:500")
public class YvrApiModule extends BaseModule {
	
	@Inject("java:$conf.getInt('topic.pageSize', 15)")
	protected int pageSize;

	/**
	 * 分页获取帖子列表
	 * @param page 页数,默认为1
	 * @param type 参数名叫tab,默认是ask,如果传all,也会变成ask
	 * @param limit 每页数量
	 * @param mdrender 是否渲染md
	 * 
	 * @api {get} /yvr/api/v1/topics 获取帖子列表
	 * @apiGroup Topic
	 * @apiVersion 1.0.0
	 * 
	 * @apiParam {int} 		[page=1] 页数,默认为1
	 * @apiParam {String} 	[tab=ask] 分类
	 * @apiParam {int} 		[limit=10] 分页
	 * @apiParam {boolean} 	[mdrender=true] 是否渲染Markdown
	 * 
	 * @apiSuccess {Object[]} data 帖子列表数据
	 * @apiSuccess {String} data.id 	唯一标示符
	 * @apiSuccess {String} data.title 	标题
	 * @apiSuccess {String} data.tab 	类型
	 * @apiSuccess {String} data.content 内容
	 * @apiSuccess {String} [data.last_reply_at] 最后回复时间
	 * @apiSuccess {boolean} data.top 	是否置顶
	 * @apiSuccess {boolean} data.good 	是否为精华帖
	 * @apiSuccess {int}	data.reply_count 总回复数量
	 * @apiSuccess {int}	data.visit_count 总浏览数量
	 * @apiSuccess {Object} data.author 作者信息
	 * @apiSuccess {String} data.author.id 作者id
	 * @apiSuccess {String} data.author.loginname 作者登陆名
	 * 
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
		Set<String> ids = jedis().zrevrangeByScore(RKEY_TOPIC_UPDATE + type, System.currentTimeMillis(), 0, pager.getOffset(), pager.getPageSize());
		for (String id : ids) {
			Topic topic = dao.fetch(Topic.class, id);
			if (topic == null)
				continue;
			list.add(_topic(topic, authors, mdrender));
		}
		return _map("data", list);
	}
	
	/**
	 * @api {get} /yvr/api/v1/topic/:id 获取帖子的详细数据
	 * @apiGroup Topic
	 * @apiVersion 1.0.0
	 * 
	 * @apiParam {String}	id 					帖子id
	 * @apiParam {boolean} 	[mdrender=true] 	是否渲染Markdown
	 * 
	 * @apiSuccess {Object[]} data 				帖子数据
	 * @apiSuccess {String} data.id 			唯一标示符
	 * @apiSuccess {String} data.title 			标题
	 * @apiSuccess {String} data.tab 			类型
	 * @apiSuccess {String} data.content 		内容
	 * @apiSuccess {String} [data.last_reply_at] 最后回复时间
	 * @apiSuccess {boolean} data.top 			是否置顶
	 * @apiSuccess {boolean} data.good 			是否为精华帖
	 * @apiSuccess {int}	data.reply_count 	总回复数量
	 * @apiSuccess {int}	data.visit_count 	总浏览数量
	 * @apiSuccess {Object} data.author 		作者信息
	 * @apiSuccess {String} data.author.id 		作者id
	 * @apiSuccess {String} data.author.loginname 作者登陆名
	 * @apiSuccess {Object[]} [data.replies] 	回复列表
	 * @apiSuccess {String} data.replies.id		回复id
	 * @apiSuccess {String} data.replies.author	回复的作者
	 * @apiSuccess {String} data.replies.author.id 回复的作者的id
	 * @apiSuccess {String} data.replies.author.loginname 回复的作者的登陆名称
	 * @apiSuccess {String} data.replies.content 回复的内容
	 * @apiSuccess {String} data.replies.ups	点赞数
	 * @apiSuccess {Object} data.replies.author 回帖作者信息
	 * @apiSuccess {String} data.replies.create_at 回帖时间
	 * @apiSuccess {String} data.replies.author.id 		作者id
	 * @apiSuccess {String} data.replies.author.loginname 作者登陆名
	 * 
	 * @apiError 404 The <code>id</code> of the Topic was not found.
	 */
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
		for (TopicReply reply : dao.query(TopicReply.class, Cnd.where("topicId", "=", id).asc("createTime"))) {
			dao.fetchLinks(reply, null);
			reply.setUps(jedis().zrange(RKEY_REPLY_LIKE + reply.getId(), 0, System.currentTimeMillis()));
			
			NutMap re = new NutMap();
			re.put("id", reply.getId());
			re.put("author", _author(reply.getAuthor()));
			
			re.put("content", "false".equals(mdrender) ? reply.getContent() : Markdowns.toHtml(reply.getContent(), urlbase));
			re.put("ups", new ArrayList<String>(reply.getUps()));
			re.put("create_at", _time(reply.getCreateTime()));
			replies.add(re);
		}
		
		tp.put("replies", replies);
		
		jedis().zincrby(RKEY_TOPIC_VISIT, 1, topic.getId());
		return _map("data", tp);
	}

	/**
	 * @api {post} /yvr/api/v1/accesstoken 测试访问凭证
	 * @apiGroup User
	 * @apiVersion 1.0.0
	 * @apiUse TOKEN
	 * @apiUse TOKEN_ERROR
	 * 
	 * @apiSuccess {String} success=true 	成功
	 * @apiSuccess {String} id 				用户id
	 * @apiSuccess {String} loginname		用户登陆名
	 * 
	 */
	@At("/accesstoken")
	@Aop("redis")
	public Object checkAccessToken(@Param("accesstoken")String accesstoken) {
		if (Strings.isBlank(accesstoken) || accesstoken.length() > 128)
			return HTTP_403;
		String uname = jedis().hget(RKEY_USER_ACCESSTOKEN2, accesstoken.toLowerCase());
		if (uname == null)
			return HTTP_403;
		return _map("success", true, "loginname", uname, "id", jedis().hget(RKEY_USER_ACCESSTOKEN3, accesstoken.toLowerCase()), "avatar_url", _avatar_url(uname));
	}
	
	/**
	 * @api {get} /yvr/api/v1/user/:id 获取用户信息
	 * 
	 * @apiGroup User
	 * @apiVersion 1.0.0
	 * @apiParam {String} id 用户id
	 * 
	 * @apiSuccess {Object} data 用户数据
	 * @apiSuccess {String} data.loginname 	用户登陆名称
	 * @apiSuccess {String} data.score 		用户积分
	 * @apiSuccess {String} data.avatar_url 头像地址
	 * @apiSuccess {Object[]} [data.recent_topics] 	最近发表的帖子
	 * @apiSuccess {String} data.recent_topics.id 	帖子id
	 * @apiSuccess {String} data.recent_topics.title 帖子标题
	 * @apiSuccess {Object[]} [data.recent_replies] 最近回复的帖子
	 * @apiSuccess {String} data.recent_replies.id 	帖子id
	 * @apiSuccess {String} data.recent_replies.title 帖子标题
	 * 
	 * @apiSuccess {String} data.create_at  注册时间
	 * 
	 * @apiError 404 The <code>id</code> of the User was not found.
	 * 
	 */
	@At("/user/?")
	@GET
	public Object user(String loginname) {
		User user = dao.fetch(User.class, loginname);
		if (user == null)
			return HTTP_404;
		Map<Integer, UserProfile> authors = new HashMap<Integer, UserProfile>();
		List<NutMap> recent_topics = new ArrayList<NutMap>();
		for (Topic topic : yvrService.getRecentTopics(user.getId())) {
			recent_topics.add(_topic(topic, authors, null));
		}
		List<NutMap> recent_replies = new ArrayList<NutMap>();
		for (Topic topic : yvrService.getRecentReplyTopics(user.getId())) {
			recent_replies.add(_topic(topic, authors, null));
		}
		return _map("data", _map("loginname", loginname,
				"avatar_url", _avatar_url(loginname), 
				"recent_topics", recent_topics,
				"recent_replies", recent_replies,
				"create_at", _time(user.getCreateTime()),
				"score", yvrService.getUserScore(user.getId())));
	}
	
	/**
	 * @api {post} /yvr/api/v1/topics 发表帖子, 以json格式提交数据
	 * @apiGroup Topic
	 * @apiVersion 1.0.0
	 * 
	 * @apiUse TOKEN
	 * @apiUse TOKEN_ERROR
	 * 
	 * @apiParam {String} title		标题
	 * @apiParam {String} content 	内容
	 * @apiParam {String} [tab=ask] 类型,默认为问答
	 * 
	 * @apiSuccess {boolean} success 是否成功
	 * @apiSuccess {String} [topic_id] 成功时返回帖子的Id
	 * @apiSuccess {String} [message] 失败时返回原因
	 */
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
	
	/**
	 * @api {post} /yvr/api/v1/topic/:id/replies 发表回复, 以json格式提交数据
	 * @apiGroup Topic
	 * @apiVersion 1.0.0
	 * 
	 * @apiUse TOKEN
	 * @apiUse TOKEN_ERROR
	 * 
	 * @apiParam {String} id 		帖子id
	 * @apiParam {String} content 	内容
	 * @apiParam {String} [reply_id] 回复哪条内容
	 * 
	 * @apiSuccess {boolean} success 是否成功
	 * @apiSuccess {String} [reply_id] 成功时返回回复的Id
	 * @apiSuccess {String} [message] 失败时返回原因
	 */
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
	
	/**
	 * @api {post} /yvr/api/v1/reply/:id/ups 点赞或取消点赞
	 * @apiGroup Topic
	 * @apiVersion 1.0.0
	 * 
	 * @apiUse TOKEN
	 * @apiUse TOKEN_ERROR
	 * 
	 * @apiParam {String} id 	回复的id
	 * @apiSuccess {boolean} success 是否成功
	 * @apiSuccess {String} [action] 点赞成功为up,取消点赞成功为down
	 * @apiSuccess {String} [message] 失败时返回原因
	 */
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
	
	/**
	 * @api {get} /yvr/api/v1/message/count 获取用户的未读消息数量
	 * @apiGroup User
	 * @apiVersion 1.0.0
	 * 
	 * @apiUse TOKEN
	 * @apiUse TOKEN_ERROR
	 * 
	 * @apiSuccess {int} data 未读消息数量
	 * 
	 */
	@GET
	@At("/message/count")
	public Object msgCount() {
		return _map("data", 0);
	}
	
	/**
	 * @api {get} /yvr/api/v1/message/count 获取用户的消息
	 * @apiGroup User
	 * @apiVersion 1.0.0
	 * 
	 * @apiUse TOKEN
	 * @apiUse TOKEN_ERROR
	 * 
	 * @apiSuccess {Object} data 用户消息
	 * @apiSuccess {Object[]} [data.has_read_messages] 已读消息列表,当前总是为空
	 * @apiSuccess {String} data.has_read_messages.id 消息id
	 * @apiSuccess {String} data.has_read_messages.type 消息类型
	 * @apiSuccess {String} data.has_read_messages.content 消息内容
	 * @apiSuccess {String} [data.has_read_messages.topic_id] 关联的帖子Id
	 * @apiSuccess {Object[]} [data.hasnot_read_messages] 未读消息列表,当前总是为空
	 * @apiSuccess {String} data.hasnot_read_messages.id 消息id
	 * @apiSuccess {String} data.hasnot_read_messages.type 消息类型
	 * @apiSuccess {String} data.hasnot_read_messages.content 消息内容
	 * @apiSuccess {String} [data.hasnot_read_messages.topic_id] 关联的帖子Id
	 * 
	 */
	@GET
	@At("/messages")
	public Object getMessages() {
		return _map("data", _map("has_read_messages", Collections.EMPTY_LIST, "hasnot_read_messages", Collections.EMPTY_LIST));
	}
	
	/**
	 * @api {get} /yvr/api/v1/message/mark_all 标记所有消息为已读
	 * @apiGroup User
	 * @apiVersion 1.0.0
	 * 
	 * @apiUse TOKEN
	 * @apiUse TOKEN_ERROR
	 * 
	 * @apiSuccess {boolean} success 成功与否
	 */
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
		tp.put("title", topic.getTitle().replace('&', ' ').replace('\'', ' '));//需要干掉特殊字符
		if (topic.getLastComment() != null)
			tp.put("last_reply_at", _time(topic.getLastComment().getCreateTime()));
		tp.put("good", topic.isGood());
		tp.put("top", topic.isTop());
		tp.put("reply_count", topic.getReplyCount());
		tp.put("visit_count", topic.getVisitCount());
		tp.put("create_at", _time(topic.getCreateTime()));
		UserProfile profile = topic.getAuthor();
		if (profile != null) {
			profile.setScore(yvrService.getUserScore(topic.getUserId()));
		}
		tp.put("author", _author(profile));
		return tp;
	}
}
