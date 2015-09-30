package net.wendal.nutzbook.service.yvr;

import static net.wendal.nutzbook.bean.CResult._fail;
import static net.wendal.nutzbook.bean.CResult._ok;
import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.wendal.nutzbook.bean.CResult;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.bean.yvr.TopicType;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.plugins.zbus.MsgBus;

import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

@IocBean(create="init")
public class YvrService {

	@Inject 
	protected Dao dao;
	
	// 用于查询Topic和TopicReply时不查询content属性
	protected Dao daoNoContent;
	
	@Inject
	protected TopicSearchService topicSearchService;
	
	@Inject
	protected MsgBus bus;
	
	@Aop("redis")
	public void fillTopic(Topic topic, Map<Integer, UserProfile> authors) {
		if (topic.getUserId() == 0)
			topic.setUserId(1);
		topic.setAuthor(_cacheFetch(authors, topic.getUserId()));
		Double reply_count = jedis().zscore("t:reply:count", topic.getId());
		topic.setReplyCount(reply_count == null ? 0 : reply_count.intValue());
		if (topic.getReplyCount() > 0) {
			String replyId = jedis().hget("t:reply:last", topic.getId());
			TopicReply reply = daoNoContent.fetch(TopicReply.class, replyId);
			if (reply != null) {
				if (reply.getUserId() == 0)
					reply.setUserId(1);
				reply.setAuthor(_cacheFetch(authors, reply.getUserId()));
				topic.setLastComment(reply);
			}
		}
		Double visited = jedis().zscore("t:visit", "" + topic.getId());
		topic.setVisitCount((visited == null) ? 0 : visited.intValue());
	}
	
	protected UserProfile _cacheFetch(Map<Integer, UserProfile> authors, int userId) {
		UserProfile author = authors.get(userId);
		if (author == null) {
			author = dao.fetch(UserProfile.class, userId);
			if (author != null) {
				dao.fetchLinks(author, null);
			}
			authors.put(userId, author);
		}
		return author;
	}
	
	@Aop("redis")
	public String accessToken(UserProfile profile) {
		String loginname = profile.getLoginname();
		String at = jedis().hget("u:accesstoken", loginname);
		if (at == null) {
			// 双向绑定
			at = R.UU32();
			jedis().hset("u:accesstoken", loginname, at);
			jedis().hset("u:accesstoken2", at, loginname);
			jedis().hset("u:accesstoken3", at, ""+profile.getUserId());
		}
		return at;
	}
	
	@Aop("redis")
	public int getUserByAccessToken(String at) {
		String uid_str = jedis().hget("u:accesstoken3", at);
		if (uid_str == null)
			return -1;
		return Integer.parseInt(uid_str);
	}

	@Aop("redis")
	public CResult add(Topic topic, int userId) {
		if (userId < 1) {
			return _fail("请先登录");
		}
		if (Strings.isBlank(topic.getTitle()) || topic.getTitle().length() > 100 || topic.getTitle().length() < 5) {
			return _fail("标题长度不合法");
		}
		if (Strings.isBlank(topic.getContent()) || topic.getContent().length() > 20000) {
			return _fail("内容不合法");
		}
		if (topic.getTags() != null && topic.getTags().size() > 10) {
			return _fail("最多只能有10个tag");
		}
		if (0 != dao.count(Topic.class, Cnd.where("title", "=", topic.getTitle().trim()))) {
			return _fail("相同标题已经发过了");
		}
		topic.setTitle(Strings.escapeHtml(topic.getTitle().trim()));
		topic.setUserId(userId);
		topic.setTop(false);
		if (topic.getType() == null)
			topic.setType(TopicType.ask);
		dao.insert(topic);
		try {
			topicSearchService.add(topic);
		} catch (Exception e) {
		}
		// 如果是ask类型,把帖子加入到 "未回复"列表
		if (TopicType.ask.equals(topic.getType())) {
			jedis().zadd("t:noreply", System.currentTimeMillis(), topic.getId());
		}
		jedis().zadd("t:update:" + topic.getType(), System.currentTimeMillis(), topic.getId());
		return _ok(topic.getId());
	}

	@Aop("redis")
	public CResult addReply(final String topicId, final TopicReply reply, final int userId) {
		if (userId < 1)
			return _fail("请先登录");
		if (reply == null || reply.getContent() == null || reply.getContent().trim().isEmpty()) {
			return _fail("内容不能为空");
		}
		final String cnt = reply.getContent().trim();
		if (cnt.length() < 2 || cnt.length() > 10000) {
			return _fail("内容太长或太短了");
		}
		final Topic topic = dao.fetch(Topic.class, topicId); // TODO 改成只fetch出type属性
		if (topic == null) {
			return _fail("主题不存在");
		}
		reply.setTopicId(topicId);
		reply.setUserId(userId);
		dao.insert(reply);
		// 更新topic的时间戳, 然后根据返回值确定是否需要从t:noreply中删除该topic
		Long re = jedis().zadd("t:update:" + topic.getType(), reply.getCreateTime().getTime(), topicId);
		if (re != null && re.intValue() != 1) {
			jedis().zrem("t:noreply", topicId);
		}
		jedis().hset("t:reply:last", topicId, reply.getId());
		jedis().zincrby("t:reply:count", 1, topicId);
		
		bus.add(new Callable<Object>() {
			public Object call() throws Exception {
				String replyAuthorName = dao.fetch(User.class, userId).getName();
				// 通知原本的作者
				if (topic.getUserId() != userId) {
					String alert = replyAuthorName+"回复了您的帖子";
					pushUser(topic.getUserId(), alert, topicId);
				}
				
				Set<String> ats = findAt(cnt, 5);
				for (String at : ats) {
					User user = dao.fetch(User.class, at);
					if (user == null)
						continue;
					if (topic.getUserId() == user.getId())
						continue; // 前面已经发过了
					if (userId == user.getId())
						continue; // 自己@自己, 忽略
					String alert = replyAuthorName + "在帖子回复中@了你";
					pushUser(user.getId(), alert, topicId);
				}
				return null;
			}
		});
		return _ok(reply.getId());
	}
	
	@Aop("redis")
	public CResult replyUp(String replyId, int userId) {
		if (userId < 1)
			return _fail("你还没登录呢");
		if (1 != dao.count(TopicReply.class, Cnd.where("id", "=", replyId))) {
			return _fail("没这条评论");
		}
		String key = "t:like:" + replyId;
		Double t = jedis().zscore(key, "" + userId);
		if (t != null) {
			jedis().zrem(key, userId + "");
			return _ok("down");
		} else {
			jedis().zadd(key, System.currentTimeMillis(), userId + "");
			return _ok("up");
		}
	}
	
	protected void pushUser(int userId, String alert, String topic_id) {
		Map<String, String> extras = new HashMap<String, String>();
		extras.put("topic_id", topic_id);
		AndroidNotification android = AndroidNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		IosNotification ios = IosNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		Notification notif = Notification.newBuilder().addPlatformNotification(android).addPlatformNotification(ios).build();
		cn.jpush.api.push.model.PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all());
		builder.setAudience(Audience.alias("u_"+ userId));
		builder.setNotification(notif);
		bus.event(builder.build());
	}
	
	public List<Topic> getRecentTopics(int userId) {
		List<Topic> recent_topics = daoNoContent().query(Topic.class, Cnd.where("userId", "=", userId).desc("createTime"), dao.createPager(1, 5));

		Map<Integer, UserProfile> authors = new HashMap<Integer, UserProfile>();
		if (!recent_topics.isEmpty()) {
			for (Topic topic : recent_topics) {
				fillTopic(topic, authors);
			}
		}
		return recent_topics;
		
	}
	
	public List<Topic> getRecentReplyTopics(int userId) {

		Map<Integer, UserProfile> authors = new HashMap<Integer, UserProfile>();
		Sql sql = Sqls.queryString("select DISTINCT topicId from t_topic_reply $cnd").setEntity(dao.getEntity(TopicReply.class)).setVar("cnd", Cnd.where("userId", "=", userId).desc("createTime"));
		sql.setPager(dao.createPager(1, 5));
		String[] replies_topic_ids = dao.execute(sql).getObject(String[].class);
		List<Topic> recent_replies = new ArrayList<Topic>();
		for (String topic_id : replies_topic_ids) {
			Topic _topic = dao.fetch(Topic.class, topic_id);
			if (_topic == null)
				continue;
			recent_replies.add(_topic);
		}
		if (!recent_replies.isEmpty()) {
			for (Topic topic : recent_replies) {
				fillTopic(topic, authors);
			}
		}
		return recent_replies;
	}
	
	public Dao daoNoContent() {
		return daoNoContent;
	}
	
	public void init() {
		daoNoContent = Daos.ext(dao, FieldFilter.locked(Topic.class, "content").set(TopicReply.class, null, "content", false));
	}
	
	static Pattern atPattern = Pattern.compile("@([a-zA-Z0-9\\_]{4,20}\\s)");
	
	public static void main(String[] args) {
		String cnt = "@wendal @zozoh 这样可以吗?@qq_addfdf";
		System.out.println(findAt(cnt, 100));
	}
	
	public static Set<String> findAt(String cnt, int limit) {
		Set<String> ats = new HashSet<String>();
		Matcher matcher = atPattern.matcher(cnt+" ");
		int start = 0;
		int end = 0;
		while (end < cnt.length() && matcher.find(end)) {
			start = matcher.start();
			end = matcher.end();
			ats.add(cnt.substring(start+1, end-1).trim().toLowerCase());
			if (limit <= ats.size())
				break;
		}
		return ats;
	}
}
