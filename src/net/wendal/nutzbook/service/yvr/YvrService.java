package net.wendal.nutzbook.service.yvr;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.util.Map;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;

import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(create="init")
public class YvrService {

	@Inject Dao dao;
	
	// 用于查询Topic和TopicReply时不查询content属性
	protected Dao daoNoContent;
	
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
	
	public Dao daoNoContent() {
		return daoNoContent;
	}
	
	public void init() {
		daoNoContent = Daos.ext(dao, FieldFilter.locked(Topic.class, "content").set(TopicReply.class, null, "content", false));
	}
}
