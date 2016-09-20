package net.wendal.nutzbook.module.admin;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.page.Pagination;
import net.wendal.nutzbook.service.RoleService;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@At("/admin/user")
@IocBean
public class AdminUserModule extends BaseModule {

	@Inject
	private RoleService roleService;

	@At
	@Ok("fm:templates.admin.user.list")
	@RequiresPermissions(value = { "user:view", "user:update", "user:add" }, logical = Logical.OR)
	public Pagination list(@Param(value = "pageNumber", df = "1") int pageNumber) {
		return userService.getListByPager(pageNumber);
	}

	@At
	@Ok("json")
	@RequiresPermissions("user:update")
	public NutMap lock(@Param("id") int id, @Param("lock") boolean lock) {
		userService.update(Chain.make("locked", lock), Cnd.where("id", "=", id));
		return NutMap.NEW().addv("type", "success").addv("content", "OK");
	}

	@At
	@Ok("fm:templates.admin.user.add")
	@RequiresPermissions("user:add")
	public void add() {

	}

	@At
	@Ok("fm:templates.admin.user.edit")
	@RequiresPermissions("user:edit")
	public User edit(@Param("id") int id, HttpServletRequest req) {
		req.setAttribute("roleList", roleService.roleList());
		return userService.fetch(id);
	}
}
