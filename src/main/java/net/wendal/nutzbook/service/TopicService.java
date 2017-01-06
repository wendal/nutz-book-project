package net.wendal.nutzbook.service;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.List;

import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.page.Pagination;
import net.wendal.nutzbook.util.RedisKey;

import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdNameEntityService;

@IocBean(fields = "dao")
public class TopicService extends IdNameEntityService<Topic> implements RedisKey {

	public Pagination getListByPager(int pageNumber) {
		Pager pager = dao().createPager(pageNumber, 20);
		List<Topic> list = dao().query(getEntityClass(), null, pager);
		pager.setRecordCount(dao().count(getEntityClass(), null));
		return new Pagination(pageNumber, 20, pager.getRecordCount(), list);
	}

	@Aop("redis")
	public int delete(String id) {
		Topic topic = dao().fetch(getEntityClass(), id);
		jedis().zrem(RKEY_TOPIC_NOREPLY, id);
		jedis().zrem(RKEY_TOPIC_UPDATE + topic.getType(), id);
		jedis().zrem(RKEY_TOPIC_UPDATE_ALL, id);
		return dao().delete(getEntityClass(), id);
	}

}
