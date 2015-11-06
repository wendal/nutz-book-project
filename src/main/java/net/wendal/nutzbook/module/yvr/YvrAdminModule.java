package net.wendal.nutzbook.module.yvr;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicType;
import net.wendal.nutzbook.module.BaseModule;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.http.Http;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
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
			Long count = jedis().zcount(RKEY_TOPIC_UPDATE+tt.name(), 0, System.currentTimeMillis());
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
}
