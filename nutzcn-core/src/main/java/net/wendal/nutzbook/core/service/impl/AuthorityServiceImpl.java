package net.wendal.nutzbook.core.service.impl;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.LoopException;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

import net.wendal.nutzbook.core.bean.Permission;
import net.wendal.nutzbook.core.bean.Role;
import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.service.AuthorityService;
import net.wendal.nutzbook.core.service.UserService;

@IocBean(name = "authorityService")
public class AuthorityServiceImpl implements AuthorityService {

	private static final Log log = Logs.get();

	@Inject
	protected Dao dao;
	
	protected UserService userService;

	public void initFormPackage(String pkg) {
		// 搜索@RequiresPermissions注解, 初始化权限表
		// 搜索@RequiresRoles注解, 初始化角色表
		final Set<String> permissions = new HashSet<String>();
		final Set<String> roles = new HashSet<String>();
		for (Class<?> klass : Scans.me().scanPackage(pkg)) {
			for (Method method : klass.getMethods()) {
				RequiresPermissions rp = method.getAnnotation(RequiresPermissions.class);
				if (rp != null && rp.value() != null) {
					for (String permission : rp.value()) {
						if (permission != null && !permission.endsWith("*"))
							permissions.add(permission);
					}
				}
				NutzRequiresPermissions nutzrp = method.getAnnotation(NutzRequiresPermissions.class);
				if (nutzrp != null && nutzrp.value() != null) {
					for (String permission : nutzrp.value()) {
						if (permission != null && !permission.endsWith("*"))
							permissions.add(permission);
					}
				}
				RequiresRoles rr = method.getAnnotation(RequiresRoles.class);
				if (rr != null && rr.value() != null) {
					for (String role : rr.value()) {
						roles.add(role);
					}
				}
			}
		}
		log.debugf("pkg=%s found permissions=%s", pkg, permissions);
		log.debugf("pkg=%s found roles=%s", pkg, roles);


        for (String permission : permissions) {
            roles.add(permission.split("[:]")[0]);
        }
		// 把全部权限查出来一一检查
		dao.each(Permission.class, null, new Each<Permission>() {
			public void invoke(int index, Permission ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				permissions.remove(ele.getName());
			}
		});
		dao.each(Role.class, null, new Each<Role>() {
			public void invoke(int index, Role ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				roles.remove(ele.getName());
			}
		});
		for (String role : roles) {
			addRole(role);
		}
		for (String permission : permissions) {
            addPermission(permission);
        }
		// 刷新缓存
        dao.update(Role.class, Chain.make("name", "XXX"), Cnd.where("name", "=", "XXX"));
        dao.update(Permission.class, Chain.make("name", "XXX"), Cnd.where("name", "=", "XXX"));
	}

	public void checkBasicRoles() {
	    User admin = dao.fetch(User.class, "admin");
	    // 检查一下admin的权限
        Role adminRole = dao.fetch(Role.class, "admin");
        if (adminRole == null) {
            adminRole = addRole("admin");
        }
        // admin账号必须存在与admin组
        if (0 == dao.count("t_user_role", Cnd.where("u_id", "=", admin.getId()).and("role_id", "=", adminRole.getId()))) {
            dao.insert("t_user_role", Chain.make("u_id", admin.getId()).add("role_id", adminRole.getId()));
        }
        // admin组必须有authority:* 也就是权限管理相关的权限
        List<Record> res = dao.query("t_role_permission", Cnd.where("role_id", "=", adminRole.getId()));
        Cnd cnd = Cnd.where("name", "like", "authority:%");
        cnd.or("name", "like", "user:%");
        cnd.or("name", "like", "configure:%");
        cnd.or("name", "like", "topic:%");
        OUT: for (Permission permission : dao.query(Permission.class, cnd, null)) {
            for (Record re : res) {
                if (re.getInt("permission_id") == permission.getId())
                    continue OUT;
            }
            dao.insert("t_role_permission", Chain.make("role_id", adminRole.getId()).add("permission_id", permission.getId()));
        }
	}

	public void addPermission(String permission) {
		Permission p = new Permission();
		p.setName(permission);
		p.setUpdateTime(new Date());
		p.setCreateTime(new Date());
		dao.insert(p);
	}

	public Role addRole(String role) {
		Role r = new Role();
		r.setName(role);
		r.setUpdateTime(new Date());
		r.setCreateTime(new Date());
		return dao.insert(r);
	}
}
