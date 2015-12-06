package net.wendal.nutzbook.module.admin;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.Message;
import net.wendal.nutzbook.page.Pagination;
import net.wendal.nutzbook.service.TopicService;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@At("/admin/topic")
@IocBean
public class AdminTopicModule {

	@Inject
	private TopicService topicService;

	@At
	@Ok("fm:templates.admin.topic.list")
	@RequiresPermissions(value = { "topic:view", "topic:update" }, logical = Logical.OR)
	public Pagination list(@Param(value = "pageNumber", df = "1") int pageNumber) {
		return topicService.getListByPager(pageNumber);
	}

	@At
	@Ok("json")
	@RequiresPermissions(value = { "topic:delete" })
	public Message delete(@Param("id") String id, HttpServletRequest req) {
		topicService.delete(id);
		return Message.success("ok", req);
	}
}
