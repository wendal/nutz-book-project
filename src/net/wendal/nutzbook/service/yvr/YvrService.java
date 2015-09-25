package net.wendal.nutzbook.service.yvr;

import static net.wendal.nutzbook.bean.CResult._fail;
import static net.wendal.nutzbook.bean.CResult._ok;
import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.util.Map;

import net.wendal.nutzbook.bean.CResult;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.bean.yvr.TopicType;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;

@IocBean(create="init")
public class YvrService {

	@Inject 
	protected Dao dao;
	
	// 用于查询Topic和TopicReply时不查询content属性
	protected Dao daoNoContent;
	
	@Inject
	protected TopicSearchService topicSearchService;
	
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
	public CResult addReply(String topicId, TopicReply reply, int userId) {
		if (userId < 1)
			return _fail("请先登录");
		if (reply == null || reply.getContent() == null || reply.getContent().trim().isEmpty()) {
			return _fail("内容不能为空");
		}
		String cnt = reply.getContent().trim();
		if (cnt.length() < 2 || cnt.length() > 10000) {
			return _fail("内容太长或太短了");
		}
		Topic topic = dao.fetch(Topic.class, topicId); // TODO 改成只fetch出type属性
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
	
	
	public Dao daoNoContent() {
		return daoNoContent;
	}
	
	public void init() {
		daoNoContent = Daos.ext(dao, FieldFilter.locked(Topic.class, "content").set(TopicReply.class, null, "content", false));
	}
}
