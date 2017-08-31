package net.wendal.nutzbook.yvr.module;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.http.Http;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.yvr.bean.Topic;
import net.wendal.nutzbook.yvr.bean.TopicType;
import net.wendal.nutzbook.yvr.service.TopicSearchService;
import net.wendal.nutzbook.yvr.service.YvrService;
import redis.clients.jedis.Jedis;

@At("/yvr/admin")
@IocBean
public class YvrAdminModule extends BaseModule{

    @Inject
    protected YvrService yvrService;
    
    @Inject
    protected TopicSearchService topicSearchService;

	@POST
	@RequiresPermissions("topic:update")
	@At
	@Aop("redis")
	@Ok("json")
	public NutMap update(@Param("..")Topic topic, @Param("opt")String opt) {
		if (topic == null)
			return _map("ok", false).setv("msg", "参数不合法");
		if (Strings.isBlank(topic.getId()) || opt == null || !opt.matches("^(good|top|type|lock|title)$"))
		    return _map("ok", false).setv("msg", "参数不合法");
		Topic old = dao.fetch(Topic.class, topic.getId());
		if (old == null)
		    return _map("ok", false).setv("msg", "帖子不存在");
		topic.setUpdateTime(new Date());
		Daos.ext(dao, FieldFilter.create(Topic.class, opt + "|updateTime", true)).update(topic);

        Jedis jedis = jedis();
		if ("good".equals(opt)) {
			if (topic.isGood()) {
				jedis.zadd(RKEY_TOPIC_UPDATE + "good", System.currentTimeMillis(), topic.getId());
			} else {
				jedis.zrem(RKEY_TOPIC_UPDATE + "good", topic.getId());
			}
		}
		else if ("top".equals(opt)) {
			if (topic.isTop()) {
			    jedis.zrem(RKEY_TOPIC_UPDATE+old.getType(), topic.getId());
			    jedis.zrem(RKEY_TOPIC_UPDATE_ALL, topic.getId());
			    jedis.zadd(RKEY_TOPIC_TOP, System.currentTimeMillis(), topic.getId());
			}else {
			    jedis.zrem(RKEY_TOPIC_TOP, topic.getId());
			    jedis.zadd(RKEY_TOPIC_UPDATE_ALL, System.currentTimeMillis(), topic.getId());
			    jedis.zadd(RKEY_TOPIC_UPDATE+old.getType(), System.currentTimeMillis(), topic.getId());
			}
			return _map("ok", true);
		}

		// 检查一下是不是修改了type
		if (topic.getType() != null && old.getType() != null && !topic.getType().equals(old.getType())) {
			Double old_d = jedis().zscore(RKEY_TOPIC_UPDATE+old.getType(), topic.getId());
			Double new_d = jedis().zscore(RKEY_TOPIC_UPDATE+topic.getType(), topic.getId());
			if (old_d != null) {
				if (new_d == null) {
					jedis().zadd(RKEY_TOPIC_UPDATE+topic.getType(), old_d, topic.getId());
				}
				jedis().zrem(RKEY_TOPIC_UPDATE+old.getType(), topic.getId());
			} else {
				if (new_d == null) {
					jedis().zadd(RKEY_TOPIC_UPDATE+topic.getType(), old.getUpdateTime().getTime(), topic.getId());
				}
			}
		}
		yvrService.updateTopicTypeCount();
        return _map("ok", true);
	}

    @POST
    @RequiresPermissions("topic:update:tags")
    @At("/update/tags")
    @Aop("redis")
    @Ok("json")
    public boolean updateTags(@Param("id")String topicId, @Param("tags")String[] tags, @Param("update_value")String[] tags2) {
        return yvrService.updateTags(topicId, new HashSet<>(Lang.array2list(tags != null ? tags : tags2)));
    }
    

    @POST
    @RequiresPermissions("topic:update:title")
    @At("/update/title")
    @Aop("redis")
    @Ok("json")
    public boolean updateTitle(@Param("id")String topicId, @Param("update_value")String val) {
        return 0 < dao.update(Topic.class, Chain.make("title", val), Cnd.where("id", "=", topicId));
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
		dao.fetchLinks(list, "author");
		return ajaxOk(new QueryResult(list, pager));
	}

	@RequiresPermissions("topic:expstatic")
	@At("/expstatic")
	@Aop("redis")
	public void exportStatic() {
		String root = "http://127.0.0.1:8080" + Mvcs.getServletContext().getContextPath();
		String dst = "/tmp/yvr_static" + Mvcs.getServletContext().getContextPath();

		// 首页
		visitAndWrite(root, "/", dst);
		visitAndWrite(root, "/yvr/", dst);
		visitAndWrite(root, "/yvr/list/", dst);

		// 输出列表页
		for (TopicType tt : TopicType.values()) {
			Long count = jedis().zcount(RKEY_TOPIC_UPDATE+tt.name(), "-inf", "+inf");
			visitAndWrite(root, "/yvr/list/"+tt.name()+"/", dst);
			if (count != null && count.longValue() > 0) {
				for (int i = 0; i < count.intValue(); i++) {
					visitAndWrite(root, "/yvr/list/"+tt.name()+"/"+i+"/", dst);
				}
			}
		}
		// 输出帖子页
		List<Topic> topics = Daos.ext(dao, FieldFilter.create(Topic.class, "id")).query(Topic.class, null);
		for (Topic topic : topics) {
			String id = topic.getId();
			visitAndWrite(root, "/yvr/t/"+id+"/", dst);
		}

		// 用户页及用户头像
		for (User user : Daos.ext(dao, FieldFilter.create(User.class, "name")).query(User.class, null)) {
			visitAndWrite(root, "/yvr/u/"+user.getName()+"/", dst);
			visitAndWrite(root, "/yvr/u/"+user.getName()+"/avatar", dst);
		}

		// SEO页
		visitAndWrite(root, "/yvr/rss.xml", dst);
		visitAndWrite(root, "/yvr/sitemap.xml", dst);

		// TODO 拷贝rs资源及upload的图片, 或者指向CDN地址?
	}

	protected void visitAndWrite(String root, String path, String dst) {
		dst += path;
		if (path.endsWith("/")) {
			dst += "index.html";
		}
		InputStream ins = null;
		try {
			ins = Http.get(root + path).getStream();
			File f = Files.createFileIfNoExists(dst);
			Files.write(f, ins);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Streams.safeClose(ins);
		}
	}
	
	@RequiresPermissions("topic:delete")
	@POST
	@At("/topic/delete")
	@Ok("json:full")
	@Aop("redis")
	public NutMap topicDelete(String id) {
	    return yvrService.topicDelete(id);
	}
	
	@RequiresPermissions("topic:rebuild:index")
    @POST
    @At("/rebuild/index")
    @Ok("json:full")
	public NutMap rebuildIndex() throws IOException {
	    topicSearchService.rebuild();
	    return new NutMap("ok", true);
	}
	
	@RequiresPermissions("topic:rebuild:redis_list")
    @POST
    @At("/rebuild/index")
    @Ok("json:full")
    public NutMap rebuildRedisList() throws IOException {
        yvrService.rebuildRedisUpdateList();
        return new NutMap("ok", true);
    }
}
