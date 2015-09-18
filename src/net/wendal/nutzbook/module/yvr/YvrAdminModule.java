package net.wendal.nutzbook.module.yvr;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.util.List;

import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicType;
import net.wendal.nutzbook.module.BaseModule;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@At("/yvr/admin")
@IocBean
public class YvrAdminModule extends BaseModule{

	@RequiresPermissions("topic:update")
	@At
	@Aop("redis")
	public void update(@Param("..")Topic topic, @Param("opt")String opt) {
		if (topic == null)
			return;
		if (Strings.isBlank(topic.getId()) || opt == null || !opt.matches("^(good|top|type|lock)$"))
			return;
		Topic old = dao.fetch(Topic.class, topic.getId());
		if (old == null)
			return;
		Daos.ext(dao, FieldFilter.create(Topic.class, opt, true)).update(topic);
		
		// 检查一下是不是修改了type
		if (topic.getType() != null && old.getType() != null && !topic.getType().equals(old.getType())) {
			Double old_d = jedis().zscore("t:update:"+old.getType(), topic.getId());
			Double new_d = jedis().zscore("t:update:"+topic.getType(), topic.getId());
			if (old_d != null) {
				if (new_d == null) {
					jedis().zadd("t:update:"+topic.getType(), old_d, topic.getId());
				}
				jedis().zrem("t:update:"+old.getType(), topic.getId());
			} else {
				if (new_d == null) {
					jedis().zadd("t:update:"+topic.getType(), old.getUpdateTime().getTime(), topic.getId());
				}
			}
		}
	}
	
	@Ok("json")
	@At
	public Object query(@Param("..")Pager pager, @Param("type")TopicType tp) {
		Cnd cnd = tp == null ? Cnd.NEW() : Cnd.where("type", "=", tp.toString());
		cnd.desc("createTime");
		if (pager == null)
			pager = dao.createPager(1, 20);
		if (pager.getPageSize() > 20)
			pager.setPageSize(20);
		if (pager.getPageNumber() < 1)
			pager.setPageNumber(1);
		int count = dao.count(Topic.class, cnd);
		pager.setRecordCount(count);
		List<Topic> list = dao.query(Topic.class, cnd, pager);
		return new QueryResult(list, pager);
	}
}
