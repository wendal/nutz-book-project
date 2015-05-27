package net.wendal.nutzbook.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.wendal.nutzbook.bean.Permission;
import net.wendal.nutzbook.bean.Role;
import net.wendal.nutzbook.bean.User;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

/**
 * 角色/权限管理. 基本假设: 一个用户属于多种角色,拥有多种特许权限. 每种角色拥有多种权限
 * @author wendal
 *
 */
@At("/admin/authority")
@IocBean
@Ok("void")//避免误写导致敏感信息泄露到服务器外
public class AuthorityModule extends BaseModule {
	
	//---------------------------------------------
	// 查询类
	
	/**
	 * 用户列表
	 */
	@RequiresPermissions("authority:user:query")
	@At
	@Ok("json:{locked:'password|salt',ignoreNull:true}") //禁止把password和salt字段进行传输
	public Object users(@Param("query")String query, @Param("..")Pager pager) {
		return ajaxOk(query(User.class, Cnd.NEW().asc("id"), pager, null));
	}
	
	/**
	 * 角色列表
	 */
	@Ok("json")
	@RequiresPermissions("authority:role:query")
	@At
	public Object roles(@Param("query")String query, @Param("..")Pager pager) {
		return ajaxOk(query(Role.class, Cnd.NEW().asc("id"), pager, null));
	}
	
	/**
	 * 权限列表
	 */
	@Ok("json")
	@RequiresPermissions("authority:permission:query")
	@At
	public Object permissions(@Param("query")String query, @Param("..")Pager pager) {
		return ajaxOk(query(Permission.class, Cnd.NEW().asc("id"), pager, null));
	}
	

	//---------------------------------------------
	// 用户操作

	/**
	 * 更新用户所属角色/特许权限
	 */
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	@RequiresPermissions("authority:user:update")
	@At("/user/update")
	@Aop(TransAop.READ_COMMITTED)
	public void updateUser(@Param("user")User user,
						   @Param("roles")List<Long> roles, 
						   @Param("permissions")List<Long> permissions) {
		// 防御一下
		if (user == null)
			return;
		user = dao.fetch(User.class, user.getId());
		// 就在那么一瞬间,那个用户已经被其他用户删掉了呢
		if (user == null)
			return;
		if (roles != null) {
			List<Role> rs = new ArrayList<Role>(roles.size());
			for (long roleId : roles) {
				Role r = dao.fetch(Role.class, roleId);
				if (r != null) {
					rs.add(r);
				}
			}
			dao.fetchLinks(user, "roles");
			if (user.getRoles().size() > 0) {
				dao.clearLinks(user, "roles");
			}
			user.setRoles(rs);
			dao.insertRelation(user, "roles");
		}
		if (permissions != null) {
			List<Permission> ps = new ArrayList<Permission>();
			for (long permissionId : permissions) {
				Permission p = dao.fetch(Permission.class, permissionId);
				if (p != null)
					ps.add(p);
			}
			dao.fetchLinks(user, "permissions");
			if (user.getPermissions().size() > 0) {
				dao.clearLinks(user, "permissions");
			}
			user.setPermissions(ps);
			dao.insertRelation(user, "permissions");
		}
	}
	
	/**
	 * 用于显示用户-权限修改对话框的信息
	 */
	@Ok("json")
	@RequiresPermissions("authority:user:update")
	@At("/user/fetch/permission")
	public Object fetchUserPermissions(@Param("id")long id) {
		User user = dao.fetch(User.class, id);
		if (user == null)
			return ajaxFail("not such user");
		user = dao.fetchLinks(user, "permissions");
		// TODO 优化为逐步加载
		List<Permission> permissions = dao.query(Permission.class, Cnd.orderBy().asc("name"));
		NutMap data = new NutMap();
		data.put("user", user);
		data.put("permissions", permissions);
		return ajaxOk(data);
	}
	
	/**
	 * 用于显示用户-权限修改对话框的信息
	 */
	@Ok("json")
	@RequiresPermissions("authority:user:update")
	@At("/user/fetch/role")
	public Object fetchUserRoles(@Param("id")long id) {
		User user = dao.fetch(User.class, id);
		if (user == null)
			return ajaxFail("not such user");
		user = dao.fetchLinks(user, "roles");
		// TODO 优化为逐步加载
		List<Role> roles = dao.query(Role.class, Cnd.orderBy().asc("name"));
		NutMap data = new NutMap();
		data.put("user", user);
		data.put("roles", roles);
		return ajaxOk(data);
	}

	//---------------------------------------------
	// Role操作
	
	/**
	 * 新增一个角色
	 */
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	@RequiresPermissions("authority:role:add")
	@At("/role/add")
	public void addRole(@Param("..")Role role) {
		if (role == null)
			return;
		dao.insert(role); // 注意,这里并没有用insertWith, 即总是插入一个无权限的角色
	}
	
	/**
	 * 删除一个角色,其中admin角色禁止删除
	 */
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	@RequiresPermissions("authority:role:delete")
	@At("/role/delete")
	public void delRole(@Param("..")Role role) {
		if (role == null)
			return;
		role = dao.fetch(Role.class, role.getId());
		if (role == null)
			return;
		// 不允许删除admin角色
		if ("admin".equals(role.getName()))
			return;
		dao.delete(Role.class, role.getId());
	}
	
	/**
	 * 更新权限的一般信息或所拥有的权限
	 */
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	@RequiresPermissions("authority:role:update")
	@At("/role/update")
	@Aop(TransAop.SERIALIZABLE) // 关键操作,强事务操作
	public void updateRole(@Param("role")Role role, @Param("permissions")List<Long> permissions) {
		if (role == null)
			return;
		if (dao.fetch(Role.class, role.getId()) == null)
			return;
		if (!Strings.isBlank(role.getAlias()) || !Strings.isBlank(role.getDescription())) {
			Daos.ext(dao, FieldFilter.create(Role.class, "alias|desc")).update(role);
		}
		if (permissions != null) {
			List<Permission> ps = new ArrayList<Permission>();
			for (Long permission : permissions) {
				Permission p = dao.fetch(Permission.class, permission);
				if (p != null)
					ps.add(p);
			}
			// 如果有老的权限,先清空,然后插入新的记录
			// TODO 优化为直接清理中间表
			dao.fetchLinks(role, "permissions");
			if (role.getPermissions().size() > 0) {
				dao.clearLinks(role, "permissions");
			}
			role.setPermissions(ps);
			dao.insertRelation(role, "permissions");
		}
		// TODO 修改Role的updateTime
	}
	
	/**
	 * 用于显示角色-权限修改对话框的信息
	 */
	@Ok("json")
	@RequiresPermissions("authority:role:update")
	@At("/role/fetch")
	public Object fetchRolePermissions(@Param("id")long id) {
		Role role = dao.fetch(Role.class, id);
		if (role == null)
			return ajaxFail("not such role");
		role = dao.fetchLinks(role, null);
		// TODO 优化为逐步加载
		List<Permission> permissions = dao.query(Permission.class, Cnd.orderBy().asc("name"));
		NutMap data = new NutMap();
		data.put("role", role);
		data.put("permissions", permissions);
		return ajaxOk(data);
	}
	
	//--------------------------------------------------------------------
	// Permission操作
	
	/**
	 * 新增一个权限
	 */
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	@RequiresPermissions("authority:permission:add")
	@At("/permission/add")
	public void addPermission(@Param("..")Permission permission) {
		if (permission == null)
			return;
		dao.insert(permission);
	}
	
	/**
	 * 删除一个角色
	 * @param permission
	 */
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	@RequiresPermissions("authority:permission:delete")
	@At("/permission/delete")
	public void delPermission(@Param("..")Permission permission) {
		if (permission == null)
			return;
		// TODO 禁止删除authority相关的默认权限
		dao.delete(Permission.class, permission.getId());
	}
	
	/**
	 * 修改权限的一般信息
	 */
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	@RequiresPermissions("authority:permission:update")
	@At("/permission/update")
	public void updateRole(@Param("..")Permission permission) {
		if (permission == null)
			return;
		if (dao.fetch(Permission.class, permission.getId()) == null)
			return;
		permission.setUpdateTime(new Date());
		permission.setCreateTime(null);
		Daos.ext(dao, FieldFilter.create(Permission.class, null, "name", true)).update(permission);
	}
}
