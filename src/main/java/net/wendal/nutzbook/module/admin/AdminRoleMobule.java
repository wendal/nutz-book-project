package net.wendal.nutzbook.module.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.Message;
import net.wendal.nutzbook.bean.PermissionCategory;
import net.wendal.nutzbook.bean.Role;
import net.wendal.nutzbook.service.PermissionCategoryService;
import net.wendal.nutzbook.service.PermissionService;
import net.wendal.nutzbook.service.RoleService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

/**
 * @author 科技㊣²º¹³<br />
 *         2015年11月29日 下午4:48:45<br />
 *         http://www.rekoe.com<br />
 *         QQ:5382211<br />
 */
@IocBean
@At("/admin/role")
public class AdminRoleMobule {

	@Inject
	private RoleService roleService;

	@Inject
	private PermissionCategoryService permissionCategoryService;

	@Inject
	private PermissionService permissionService;

	@At
	@Ok("fm:templates.admin.user.role.list")
	@RequiresPermissions(value = "role:view")
	public Object list(@Param(value = "pageNumber", df = "1") int pageNumber) {
		return roleService.getListByPager(pageNumber);
	}

	@At
	@Ok("fm:templates.admin.user.role.add")
	@RequiresPermissions(value = "role:add")
	public List<PermissionCategory> add() {
		return permissionCategoryService.getList();
	}

	@At
	@Ok("json")
	@RequiresPermissions(value = "role:add")
	public Message save(@Param("::role.") Role role, HttpServletRequest req, @Param("authorities") Integer[] ids) {
		if (!Lang.isEmptyArray(ids)) {
			role.setPermissions(permissionService.query(Cnd.where("id", "in", ids), null));
		}
		roleService.insert(role);
		return Message.success("ok", req);
	}

	@At
	@Ok("fm:templates.admin.user.role.edit")
	@RequiresPermissions(value = "role:edit")
	public Role edit(@Param("id") int id, HttpServletRequest req) {
		req.setAttribute("pcList", permissionCategoryService.getList());
		return roleService.fetch(id);
	}

	@At
	@Ok(">>:/admin/role/list")
	@RequiresPermissions(value = "role:edit")
	public void update(@Param("::role.") Role role, @Param("authorities") Integer[] ids) {
		if (!Lang.isEmptyArray(ids)) {
			List<net.wendal.nutzbook.bean.Permission> perms = permissionService.query(Cnd.where("id", "in", ids), null);
			roleService.updateRoleRelation(role, perms);
		} else {
			roleService.update(role);
		}
	}
}
