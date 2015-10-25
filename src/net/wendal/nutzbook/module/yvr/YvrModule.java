package net.wendal.nutzbook.module.yvr;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.WhaleAdaptor;
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
import org.nutz.mvc.annotation.ReqHeader;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.view.ForwardView;
import org.nutz.mvc.view.HttpStatusView;

import net.wendal.nutzbook.bean.CResult;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.bean.yvr.TopicType;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.mvc.CsrfActionFilter;
import net.wendal.nutzbook.service.PushService;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.service.yvr.LuceneSearchResult;
import net.wendal.nutzbook.service.yvr.TopicSearchService;

@IocBean(create = "init")
@At("/yvr")
@Fail("void")
public class YvrModule extends BaseModule {

	private static final Log log = Logs.get();

	@Inject
	protected UserService userService;

	@Inject("java:$conf.getInt('topic.pageSize', 15)")
	protected int pageSize;

	@Inject("java:$conf.get('topic.image.dir')")
	protected String imageDir;

	@At({ "/", "/index" })
	@Ok("->:/yvr/list")
	public void index() {
	}

	@Inject
	protected TopicSearchService topicSearchService;
	
	@Inject
	protected PushService pushService;

	@GET
	@At
	@Ok("beetl:yvr/_add.btl")
	public Object add(HttpSession session, @Attr(scope = Scope.SESSION, value = "me") int userId) {
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
	@Filters(@By(type = CsrfActionFilter.class))
	public CResult add(@Param("..")Topic topic, @Attr(scope=Scope.SESSION, value="me")int userId) {
		return yvrService.add(topic, userId);
	}

	@At({ "/list/?", "/list/?/?", "/list" })
	@GET
	@Ok("beetl:/yvr/index.btl")
	@Aop("redis")
	public Object list(TopicType type, int page, @Attr(scope = Scope.SESSION, value = "me") int userId) {
		Pager pager = dao.createPager(page > 0 ? page : 1, pageSize);
		if (type == null)
			type = TopicType.ask;
		long now = System.currentTimeMillis();
		String zkey = RKEY_TOPIC_UPDATE + type;
		Long count = jedis().zcount(zkey, 0, now);
		List<Topic> list = new ArrayList<Topic>();
		if (count != null && count.intValue() != 0) {
			pager.setRecordCount(count.intValue());
			Set<String> ids = jedis().zrevrangeByScore(zkey, now, 0, pager.getOffset(), pager.getPageSize());
			for (String id : ids) {
				Topic topic = yvrService.daoNoContent().fetch(Topic.class, id);
				if (topic == null)
					continue;
				list.add(topic);
			}
		}
		return _process_query_list(pager, list, userId, type);
	}

	protected NutMap _process_query_list(Pager pager, List<Topic> list, int userId, TopicType type) {
		Map<Integer, UserProfile> authors = new HashMap<Integer, UserProfile>();
		for (Topic topic : list) {
			yvrService.fillTopic(topic, authors);
		}
		
		// 查一下未回复的帖子, 最近的5条的就好了
		Set<String> no_replies_ids = jedis().zrangeByScore(RKEY_TOPIC_NOREPLY, 0, Long.MAX_VALUE, 0, 5);
		List<Topic> no_replies = new ArrayList<Topic>();
		for (String topicId : no_replies_ids) {
			Topic tmp = yvrService.daoNoContent().fetch(Topic.class, topicId);
			if (tmp != null) {
				no_replies.add(tmp);
			}
		}
		
		NutMap re = new NutMap();
		re.put("list", list);
		re.put("pager", pager);
		re.put("type", type);
		re.put("types", TopicType.values());
		/**
		 * var page_start = current_page - 2 > 0 ? current_page - 2 : 1; var
		 * page_end = page_start + 4 >= pages ? pages : page_start + 4;
		 */
		int page_start = pager.getPageNumber() - 2 > 0 ? pager.getPageNumber() - 2 : 1;
		int page_end = page_start + 4 >= pager.getPageCount() ? pager.getPageCount() : page_start + 4;
		re.put("page_start", page_start);
		re.put("page_end", page_end);
		re.put("current_page", pager.getPageNumber());
		re.put("pages", pager.getPageCount());
		if (userId > 0)
			re.put("current_user", fetch_userprofile(userId));
		
		// 添加未回复的列表
		if (!no_replies.isEmpty())
			re.put("no_reply_topics", no_replies);
		return re;
	}

	@GET
	@At("/t/?")
	@Ok("beetl:yvr/_topic.btl")
	@Aop("redis")
	public Object topic(String id, @Attr(scope = Scope.SESSION, value = "me") int userId,
						@ReqHeader("User-Agent")String userAgent) {
		Topic topic = dao.fetch(Topic.class, id);
		if (topic == null) {
			return HttpStatusView.HTTP_404;
		}
		if (topic.getUserId() == 0)
			topic.setUserId(1);
		topic.setAuthor(fetch_userprofile(topic.getUserId()));
		dao.fetchLinks(topic, "replies", Cnd.orderBy().asc("createTime"));
		for (TopicReply reply : topic.getReplies()) {
			if (reply.getUserId() == 0)
				reply.setUserId(1);
			dao.fetchLinks(reply, null);
			reply.setUps(jedis().zrange(RKEY_REPLY_LIKE + reply.getId(), 0, System.currentTimeMillis()));
		}
		NutMap re = new NutMap();
		re.put("topic", topic);

		if (userId > 0) {
			String csrf = Lang.md5(R.UU16());
			Mvcs.getHttpSession().setAttribute("_csrf", csrf);
			re.put("_csrf", csrf);
			re.put("current_user", fetch_userprofile(userId));
		}
		Double visited = 0d;
		boolean flag = userId == topic.getAuthor().getUserId();
		if (!flag && userAgent != null && userAgent.length() < 1024) {
			userAgent = userAgent.toLowerCase();
			flag = userAgent.contains("robot") || userAgent.contains("spider") || userAgent.contains("bot.");
		}
		if (!flag) {
			for (TopicReply reply : topic.getReplies()) {
				if (reply.getAuthor().getUserId() == userId) {
					flag = true;
					break;
				}
			}
		}
		if (flag) {
			visited = jedis().zscore(RKEY_TOPIC_VISIT, id);
			if (visited == null)
				visited = 0d;
		}
		else {
			visited = jedis().zincrby(RKEY_TOPIC_VISIT, 1, id);
		}
		topic.setVisitCount((visited == null) ? 0 : visited.intValue());
		return re;
	}

	@AdaptBy(type = WhaleAdaptor.class)
	@POST
	@At
	@Ok("json")
	@Filters(@By(type = CsrfActionFilter.class))
	public Object upload(@Param("file") TempFile tmp, HttpServletRequest req, HttpServletResponse resp, @Attr(scope = Scope.SESSION, value = "me") int userId) throws IOException {
		resp.setContentType("application/json");
		return yvrService.upload(tmp, userId);
	}

	@Ok("raw:jpg")
	@At("/upload/?/?")
	@Fail("http:404")
	public Object image(String p, String p2) throws IOException {
		if ((p + p2).contains("."))
			return HttpStatusView.HTTP_404;
		File f = new File(imageDir, p + "/" + p2);
		return f;
	}

	@Filters(@By(type = CsrfActionFilter.class))
	@At("/t/?/reply")
	@Ok("json")
	public Object addReply(String topicId, @Param("..") TopicReply reply, @Attr(scope = Scope.SESSION, value = "me") int userId) {
		return yvrService.addReply(topicId, reply, userId);
	}

	@At("/t/?/reply/?/up")
	@Ok("json")
	public Object replyUp(String topicId, String replyId, @Attr(scope = Scope.SESSION, value = "me") int userId) {
		return yvrService.replyUp(replyId, userId);
	}

	@GET
	@At
	@Ok("beetl:/yvr/index.btl")
	@Aop("redis")
	public Object search(@Param("q") String keys, @Attr(scope = Scope.SESSION, value = "me") int userId) throws Exception {
		if (Strings.isBlank(keys))
			return new ForwardView("/yvr/list");
		List<LuceneSearchResult> results = topicSearchService.search(keys);
		List<Topic> list = new ArrayList<Topic>();
		for (LuceneSearchResult result : results) {
			Topic topic = dao.fetch(Topic.class, result.getId());
			if (topic == null)
				continue;
			topic.setTitle(result.getResult());
			list.add(topic);
		}
		Pager pager = dao.createPager(1, 30);
		pager.setRecordCount(list.size());
		return _process_query_list(pager, list, userId, TopicType.ask);
	}

	@At("/search/rebuild")
	public void rebuild() throws IOException {
		topicSearchService.rebuild();
	}
	
	@POST
	@At("/t/?/push")
	public void push(String topicId, @Attr(scope = Scope.SESSION, value = "me") int userId) {
		if (userId < 1)
			return;
		Map<String, String> extras = new HashMap<String, String>();
		extras.put("topic_id", topicId);
		extras.put("action", "open_topic");
		pushService.message(userId, "应用户要求推送到客户端打开帖子", extras);
	}

	public void init() {
		log.debug("Image Dir = " + imageDir);
		Files.createDirIfNoExists(new File(imageDir));
	}
}
