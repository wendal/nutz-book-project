package net.wendal.nutzbook.module.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.Message;
import net.wendal.nutzbook.bean.Permission;
import net.wendal.nutzbook.bean.PermissionCategory;
import net.wendal.nutzbook.page.Pagination;
import net.wendal.nutzbook.service.PermissionCategoryService;
import net.wendal.nutzbook.service.PermissionService;

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
@At("/admin/permission")
public class AdminPermissionModule {

	@Inject
	private PermissionCategoryService permissionCategoryService;

	@Inject
	private PermissionService permissionService;

	@At
	@Ok("fm:templates.admin.user.permission.list")
	@RequiresPermissions({ "permission:view" })
	public Pagination list(@Param(value = "pageNumber", df = "1") int pageNumber) {
		return permissionService.getListByPager(pageNumber);
	}

	@At
	@Ok("fm:templates.admin.user.permission.edit")
	@RequiresPermissions({ "permission:edit" })
	public List<PermissionCategory> edit(int id, HttpServletRequest req) {
		Permission permission = permissionService.fetch(id);
		req.setAttribute("permission", permission);
		return add();
	}

	@At
	@Ok(">>:/admin/permission/list.rk")
	@RequiresPermissions({ "permission:edit" })
	public boolean update(@Param("name") String name, @Param("id") String id) {
		permissionService.update(Chain.make("name", name), Cnd.where("id", "=", id));
		return true;
	}

	@At
	@Ok("fm:templates.admin.user.permission.add")
	@RequiresPermissions({ "system.permission:add" })
	public List<PermissionCategory> add() {
		List<PermissionCategory> list = permissionCategoryService.getList();
		return list;
	}

	@At
	@Ok(">>:/admin/permission/list.rk")
	@RequiresPermissions({ "permission:add" })
	public void save(@Param("name") String name) {

	}

	@SuppressWarnings("unused")
	@At
	@Ok("json")
	@RequiresPermissions({ "permission:delete" })
	public Message delete(@Param("id") int id, HttpServletRequest req) {
		Permission pc = permissionService.fetch(id);
		// permissionCategoryService.remove(id);
		return Message.success("admin.common.success", req);
	}
}
