package net.wendal.nutzbook.core.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.slog.annotation.Slog;

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.bean.Permission;
import net.wendal.nutzbook.core.bean.Role;
import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.bean.UserProfile;

/**
 * 角色/权限管理. 基本假设: 一个用户属于多种角色,拥有多种特许权限. 每种角色拥有多种权限
 * 
 * @author wendal
 *
 */
@Api(name = "权限角色管理", description = "一个用户属于多种角色,拥有多种特许权限. 每种角色拥有多种权限", match = ApiMatchMode.NONE)
@At("/admin/authority")
@IocBean
@Ok("json:full")
@AdaptBy(type=WhaleAdaptor.class)
public class AuthorityModule extends BaseModule {

    // ---------------------------------------------
    // 查询类

    /**
     * 用户列表
     */
    @NutzRequiresPermissions(value="authority:user:query", name="用户查询", tag="权限管理")
    @At
    @Ok("json:{locked:'password|salt',ignoreNull:true}") // 禁止把password和salt字段进行传输
    public Object users(@Param("query") String query, @Param("..") Pager pager) {
        return ajaxOk(query(User.class, Cnd.NEW().asc("id"), pager, null));
    }

    /**
     * 角色列表
     */
    @Ok("json")
    @NutzRequiresPermissions(value="authority:role:query", name="角色查询", tag="权限管理")
    @At
    public Object roles(@Param("query") String query, @Param("..") Pager pager) {
        return ajaxOk(query(Role.class, Cnd.NEW().asc("id"), pager, null));
    }

    /**
     * 权限列表
     */
    @Ok("json")
    @RequiresPermissions("authority:permission:query")
    @At
    public Object permissions(@Param("query") String query, @Param("..") Pager pager) {
        return ajaxOk(query(Permission.class, Cnd.NEW().asc("name"), pager, null));
    }

    // ---------------------------------------------
    // 用户操作

    @Ok("json")
    @RequiresPermissions("authority:user:add")
    @At("/user/add")
    @Slog(tag = "新增用户", before = "用户[${user.name}] ok=${re.ok}")
    public Object addUser(@Param("..") User user) { // 两个点号是按对象属性一一设置
        NutMap re = new NutMap();
        String msg = checkUser(user, true);
        if (msg != null) {
            return re.setv("ok", false).setv("msg", msg);
        }
        user = userService.add(user.getName(), user.getPassword());
        return ajaxOk(user);
    }

    @Ok("json")
    @POST
    @RequiresPermissions("authority:user:update")
    @At("/user/update/password")
    @Slog(tag = "更新用户", before = "用户[${id}] ok=${re.ok}")
    public Object updatePassword(@Param("password") String password, @Param("id") int id) {
        if (Strings.isBlank(password) || password.length() < 6)
            return ajaxFail("密码不符合要求");
        userService.updatePassword(id, password);
        return ajaxOk(null);
    }

    @RequiresPermissions("authority:user:delete")
    @At("/user/delete")
    @Aop(TransAop.READ_COMMITTED)
    @Slog(tag = "删除用户", before = "用户id[${id}]")
    public Object userDelete(@Param("id") long id) {
        long me = Toolkit.uid();
        if (me == id) {
            return ajaxFail("不能删除当前用户!!");
        }
        dao.delete(User.class, id); // 再严谨一些的话,需要判断是否为>0
        dao.clear(UserProfile.class, Cnd.where("userId", "=", id));
        return ajaxOk(null);
    }

    /**
     * 更新用户所属角色/特许权限
     */
    @POST
    @RequiresPermissions("authority:user:update")
    @At("/user/update")
    @Aop(TransAop.READ_COMMITTED)
    public void updateUser(@Param("user") User user,
                           @Param("roles") List<Long> roles,
                           @Param("permissions") List<Long> permissions) {
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
    public Object fetchUserPermissions(@Param("id") long id) {
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
    public Object fetchUserRoles(@Param("id") long id) {
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

    // ---------------------------------------------
    // Role操作

    /**
     * 新增一个角色
     */
    @POST
    @RequiresPermissions("authority:role:add")
    @At("/role/add")
    public Object addRole(@Param("..") Role role) {
        if (role == null)
            return ajaxFail("非法请求");
        dao.insert(role); // 注意,这里并没有用insertWith, 即总是插入一个无权限的角色

        return ajaxOk(null);
    }

    /**
     * 删除一个角色,其中admin角色禁止删除
     */
    @POST
    @RequiresPermissions("authority:role:delete")
    @At("/role/delete")
    public Object delRole(@Param("..") Role role) {
        if (role == null)
            return ajaxFail("非法请求");
        role = dao.fetch(Role.class, role.getId());
        if (role == null)
            return ajaxFail("角色不存在");
        // 不允许删除admin角色
        if ("admin".equals(role.getName()))
            return ajaxFail("admin角色不可以删除");
        dao.delete(Role.class, role.getId());

        return ajaxOk(null);
    }

    /**
     * 更新权限的一般信息或所拥有的权限
     */
    @POST
    @RequiresPermissions("authority:role:update")
    @At("/role/update")
    @Aop(TransAop.SERIALIZABLE) // 关键操作,强事务操作
    public Object updateRole(@Param("role") Role role, @Param("permissions") List<Long> permissions) {
        if (role == null)
            return ajaxFail("非法请求");
        if (dao.fetch(Role.class, role.getId()) == null)
            return ajaxFail("权限不存在");
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

        return ajaxOk(null);
    }

    /**
     * 用于显示角色-权限修改对话框的信息
     */
    @Ok("json")
    @RequiresPermissions("authority:role:update")
    @At("/role/fetch")
    public Object fetchRolePermissions(@Param("id") long id) {
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

    // --------------------------------------------------------------------
    // Permission操作

    /**
     * 新增一个权限
     */
    @POST
    @RequiresPermissions("authority:permission:add")
    @At("/permission/add")
    public Object addPermission(@Param("..") Permission permission) {
        if (permission == null)
            return ajaxFail("非法请求");
        dao.insert(permission);
        return ajaxOk(null);
    }

    /**
     * 删除一个角色
     * 
     * @param permission
     */
    @POST
    @RequiresPermissions("authority:permission:delete")
    @At("/permission/delete")
    public Object delPermission(@Param("..") Permission permission) {
        if (permission == null)
            return ajaxFail("非法请求");
        // TODO 禁止删除authority相关的默认权限
        dao.delete(Permission.class, permission.getId());
        return ajaxOk(null);
    }

    /**
     * 修改权限的一般信息
     */
    @POST
    @RequiresPermissions("authority:permission:update")
    @At("/permission/update")
    public Object updatePermission(@Param("permission") Permission permission) {
        if (permission == null)
            return ajaxFail("非法请求");
        if (dao.fetch(Permission.class, permission.getId()) == null)
            return ajaxFail("权限不存在");
        permission.setUpdateTime(new Date());
        permission.setCreateTime(null);
        Daos.ext(dao, FieldFilter.create(Permission.class, null, "name", true)).update(permission);
        return ajaxOk(null);
    }
    
    protected String checkUser(User user, boolean create) {
        if (user == null) {
            return "空对象";
        }
        if (create) {
            if (Strings.isBlank(user.getName()) || Strings.isBlank(user.getPassword()))
                return "用户名/密码不能为空";
        } else {
            if (Strings.isBlank(user.getPassword()))
                return "密码不能为空";
        }
        String passwd = user.getPassword().trim();
        if (6 > passwd.length() || passwd.length() > 12) {
            return "密码长度错误";
        }
        user.setPassword(passwd);
        if (create) {
            int count = dao.count(User.class, Cnd.where("name", "=", user.getName()));
            if (count != 0) {
                return "用户名已经存在";
            }
        } else {
            if (user.getId() < 1) {
                return "用户Id非法";
            }
        }
        if (user.getName() != null)
            user.setName(user.getName().trim());
        return null;
    }
}
