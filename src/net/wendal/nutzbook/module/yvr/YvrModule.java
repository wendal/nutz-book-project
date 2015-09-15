package net.wendal.nutzbook.module.yvr;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.bean.yvr.TopicType;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.mvc.CsrfActionFilter;
import net.wendal.nutzbook.service.UserService;

import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Scope;
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
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.mvc.view.HttpStatusView;

@IocBean(create="init")
@At("/yvr")
@Fail("void")
public class YvrModule extends BaseModule {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected UserService userService;
	
	@Inject("java:$conf.get(\"topic.image.dir\")")
	protected String imageDir;
	
	@At({"/", "/index"})
	@Ok(">>:/yvr/list")
	public void index() {}
	
	@GET
	@At
	@Ok("beetl:yvr/_add.btl")
	public Object add(HttpSession session, @Attr(scope=Scope.SESSION, value="me")int userId) {
		NutMap re = new NutMap();
		re.put("types", TopicType.values());
		
		String csrf = Lang.md5(R.UU16());
		session.setAttribute("_csrf", csrf);
		re.put("_csrf", csrf);
		
		re.put("current_user", fetch_userprofile(userId));
		return re;
	}

	@POST
	@At
	@Ok("json")
	@Filters(@By(type=CsrfActionFilter.class))
	@Aop("redis")
	public NutMap add(@Param("..")Topic topic,
					@Attr(scope=Scope.SESSION, value="me")int userId,
					HttpServletRequest req) {
		if (userId < 1) {
			return ajaxFail("请先登录");
		}
		if (Strings.isBlank(topic.getTitle()) || topic.getTitle().length() > 1024 || topic.getTitle().length() < 10) {
			return ajaxFail("标题长度不合法");
		}
		if (Strings.isBlank(topic.getContent()) || topic.getContent().length() > 20000) {
			return ajaxFail("内容不合法");
		}
		if (topic.getTags() != null && topic.getTags().size() > 10) {
			return ajaxFail("最多只能有10个tag");
		}
		if (0 != dao.count(Topic.class, Cnd.where("title", "=", topic.getTitle().trim()))) {
			return ajaxFail("相同标题已经发过了");
		}
		topic.setUserId(userId);
		topic.setTop(false);
		if (topic.getType() == null)
			topic.setType(TopicType.ask);
		dao.insert(topic);
		// 如果是ask类型,把帖子加入到 "未回复"列表
		if (TopicType.ask.equals(topic.getType())) {
			jedis().zadd("t:noreply", System.currentTimeMillis(), topic.getId());
		}
		jedis().zadd("t:update:"+topic.getType(), System.currentTimeMillis(), topic.getId());
		return ajaxOk(topic.getId());
	}
	
	@At({"/list/?", "/list"})
	@GET
	@Ok("beetl:/yvr/index.btl")
	@Aop("redis")
	public Object list(TopicType type, @Param("..")Pager pager,
			@Attr(scope=Scope.SESSION, value="me")int userId) {
		// TODO 按最后回复时间+创建时间排序
		if (pager == null)
			pager = dao.createPager(1, 20);
		else {
			if (pager.getPageNumber() < 1)
				pager.setPageNumber(1);
			if (pager.getPageSize() > 20 || pager.getPageSize() < 1)
				pager.setPageSize(20);
		}
		if (type == null)
			type = TopicType.ask;
		long now = System.currentTimeMillis();
		String zkey = "t:update:"+type;
		Long count = jedis().zcount(zkey, 0, now);
		List<Topic> list = new ArrayList<Topic>();
		if (count != null && count.intValue() != 0) {
			pager.setRecordCount(count.intValue());
			Set<String> ids = jedis().zrevrangeByScore(zkey, now, 0,  pager.getOffset(), pager.getPageSize());
			for (String id : ids) {
				Topic topic = dao.fetch(Topic.class, id);
				if (topic == null)
					continue;
				list.add(topic);
			}
		}
		for (Topic topic : list) {
			if (topic.getUserId() == 0)
				topic.setUserId(1);
			dao.fetchLinks(topic, null);
			dao.fetchLinks(topic.getAuthor(), null);
			Double reply_count = jedis().zscore("t:reply:count", topic.getId());
			topic.setReplyCount(reply_count == null ? 0 : reply_count.intValue());
			if (topic.getReplyCount() > 0) {
				String replyId = jedis().hget("t:reply:last", topic.getId());
				TopicReply reply = dao.fetch(TopicReply.class, replyId);
				if (reply != null) {
					if (reply.getUserId() == 0)
						reply.setUserId(1);
					dao.fetchLinks(reply, "author");
					dao.fetchLinks(reply.getAuthor(), null);
					topic.setLastComment(reply);
				}
			}
			Double visited = jedis().zscore("t:visit", ""+topic.getId());
			topic.setVisitCount((visited == null) ? 0 : visited.intValue());
		}
		NutMap re = new NutMap();
		re.put("list", list);
		re.put("pager", pager);
		re.put("type", type);
		re.put("types", TopicType.values());
		/**
		 	var page_start = current_page - 2 > 0 ? current_page - 2 : 1;
    		var page_end = page_start + 4 >= pages ? pages : page_start + 4;
		 */
		int page_start = pager.getPageNumber() - 2 > 0 ? pager.getPageNumber() - 2 : 1;
		int page_end = page_start + 4 >= pager.getPageCount() ? pager.getPageCount() : page_start + 4;
		re.put("page_start", page_start);
		re.put("page_end", page_end);
		re.put("current_page", pager.getPageNumber());
		re.put("pages", pager.getPageCount());
		re.put("current_user", fetch_userprofile(userId));
		return re;
	}
	
	@GET
	@At("/t/?")
	@Ok("beetl:yvr/_topic.btl")
	@Aop("redis")
	public Object topic(String id, HttpSession session, @Attr(scope=Scope.SESSION, value="me")int userId) {
		Topic topic = dao.fetch(Topic.class, id);
		if (topic == null) {
			return HttpStatusView.HTTP_404;
		}
		Double visited = jedis().zincrby("t:visit", 1, ""+id);
		topic.setVisitCount((visited == null) ? 0 : visited.intValue());
		if (topic.getUserId() == 0)
			topic.setUserId(1);
		dao.fetchLinks(topic, null);
		dao.fetchLinks(topic.getAuthor(), null);
		for (TopicReply reply : topic.getReplies()) {
			if (reply.getUserId() == 0)
				reply.setUserId(1);
			dao.fetchLinks(reply, null);
			dao.fetchLinks(reply.getAuthor(), null);
			reply.setUps(jedis().zrange("t:like:"+reply.getId(), 0, System.currentTimeMillis()));
		}
		NutMap re = new NutMap();
		re.put("topic", topic);
		
		String csrf = Lang.md5(R.UU16());
		session.setAttribute("_csrf", csrf);
		re.put("_csrf", csrf);
		re.put("current_user", fetch_userprofile(userId));
		return re;
	}
	
	@AdaptBy(type=UploadAdaptor.class, args={"${app.root}/WEB-INF/tmp2"})
	@POST
	@At
	@Ok("json")
	@Filters(@By(type=CsrfActionFilter.class))
	public Object upload(@Param("file")TempFile tmp,
			HttpServletRequest req,
			HttpServletResponse resp,
			@Attr(scope=Scope.SESSION, value="me")int userId) throws IOException {
		resp.setContentType("application/json");
		NutMap jsonrpc = new NutMap();
		if (userId < 1)
			return jsonrpc.setv("msg", "请先登陆!");
		if (tmp == null || tmp.getFile().length() == 0) {
			return jsonrpc.setv("msg", "空文件");
		}
		if (tmp.getFile().length() > 2*1024*1024) {
			return jsonrpc.setv("msg", "文件太大了");
		}
		String id = R.UU32();
		String path = "/" + id.substring(0, 2) + "/" + id.substring(2);
		File f = new File(imageDir + path);
		Files.createNewFile(f);
		Files.copyFile(tmp.getFile(), f);
		jsonrpc.setv("url", req.getRequestURI() + path);
		jsonrpc.setv("success", true);
		return jsonrpc;
	}
	
	@Ok("raw:jpg")
	@At("/upload/?/?")
	@Fail("http:404")
	public Object image(String p, String p2) throws IOException {
		if ((p+p2).contains("."))
			return HttpStatusView.HTTP_404;
		File f = new File(imageDir, p + "/" + p2);
		return f;
	}

	@Filters(@By(type=CsrfActionFilter.class))
	@At("/t/?/reply")
	@Ok("json")
	@Aop("redis")
	public Object addReply(String topicId, @Param("..")TopicReply reply,
			@Attr(scope=Scope.SESSION, value="me")int userId) {
		if (reply == null || reply.getContent() == null || reply.getContent().trim().isEmpty()) {
			return ajaxFail("内容不能为空");
		}
		String cnt = reply.getContent().trim();
		if (cnt.length() < 2 || cnt.length() > 10000) {
			return ajaxFail("内容太长或太短了");
		}
		Topic topic = dao.fetch(Topic.class, topicId); // TODO 改成只fetch出type属性
		if (topic == null) {
			return ajaxFail("主题不存在");
		}
		reply.setTopicId(topicId);
		reply.setUserId(userId);
		dao.insert(reply);
		// 更新topic的时间戳, 然后根据返回值确定是否需要从t:noreply中删除该topic
		Long re = jedis().zadd("t:update:"+topic.getType(), reply.getCreateTime().getTime(), topicId);
		if (re != null && re.intValue() != 1) {
			jedis().zrem("t:noreply", topicId);
		}
		jedis().hset("t:reply:last", topicId, reply.getId());
		jedis().zincrby("t:reply:count", 1, topicId);
		return ajaxOk(null);
	}
	
	
	@At("/t/?/reply/?/up")
	@Ok("json")
	@Aop("redis")
	public void replyUp(String _, String replyId, @Attr(scope=Scope.SESSION, value="me")int userId){
		if (userId < 1)
			return;
		jedis().zadd("t:like:"+replyId, System.currentTimeMillis(), userId+"");
	}
	
	
	public void init() {
		log.debug("Image Dir = " + imageDir);
		Files.createDirIfNoExists(new File(imageDir));
	}
	
	public UserProfile fetch_userprofile(int userId) {
		UserProfile profile = dao.fetch(UserProfile.class, userId);
		if (profile == null)
			return null;
		dao.fetchLinks(profile, null);
		return profile;
	}
}
