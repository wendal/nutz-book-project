package net.wendal.nutzbook.module.admin;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.Message;
import net.wendal.nutzbook.bean.PermissionCategory;
import net.wendal.nutzbook.page.Pagination;
import net.wendal.nutzbook.service.PermissionCategoryService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

/**
 * @author 科技㊣²º¹³<br />
 *         2015年12月30日 下午4:48:45<br />
 *         http://www.rekoe.com<br />
 *         QQ:5382211
 */
@IocBean
@At("/admin/permission/category")
public class AdminPermissionCategoryModule {

	@Inject
	private PermissionCategoryService permissionCategoryService;

	@At
	@Ok("fm:templates.admin.user.permission_category.list")
	@RequiresPermissions({ "permission:view" })
	public Pagination list(@Param(value = "pageNumber", df = "1") int pageNumber) {
		return permissionCategoryService.getListByPager(pageNumber);
	}

	@At
	@Ok("fm:templates.admin.user.permission_category.edit")
	@RequiresPermissions({ "permission:edit" })
	public PermissionCategory edit(String id) {
		return permissionCategoryService.fetch(id);
	}

	@At
	@Ok(">>:/admin/permission/category/list.rk")
	@RequiresPermissions({ "permission:edit" })
	public boolean update(@Param("name") String name, @Param("id") String id) {
		permissionCategoryService.update(Chain.make("name", name), Cnd.where("id", "=", id));
		return true;
	}

	@At
	@Ok("fm:templates.admin.user.permission_category.add")
	@RequiresPermissions({ "permission:add" })
	public void add() {
	}

	@At
	@Ok(">>:/admin/permission/category/list.rk")
	@RequiresPermissions({ "permission:add" })
	public void save(@Param("name") String name) {
		PermissionCategory pc = new PermissionCategory();
		pc.setLocked(false);
		pc.setName(name);
		permissionCategoryService.insert(pc);
	}

	@At
	@Ok("json")
	@RequiresPermissions({ "permission:delete" })
	public Message delete(@Param("id") String id, HttpServletRequest req) {
		PermissionCategory pc = permissionCategoryService.fetch(id);
		if (pc.isLocked()) {
			return Message.error("admin.permissionCategory.deleteLockedNotAllowed", req);
		}
		// permissionCategoryService.remove(id);
		return Message.success("admin.common.success", req);
	}
}
