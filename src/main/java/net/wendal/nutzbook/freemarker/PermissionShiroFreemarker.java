package net.wendal.nutzbook.freemarker;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.wendal.nutzbook.util.Toolkit;

import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.util.PermissionUtils;
import org.apache.shiro.util.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class PermissionShiroFreemarker implements TemplateDirectiveModel {

	private PermissionResolver permissionResolver;

	private final static String PERM = "perm";
	private Dao dao;

	public PermissionShiroFreemarker(PermissionResolver permissionResolver, Dao dao) {
		this.permissionResolver = permissionResolver;
		this.dao = dao;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		int uid = Toolkit.uid();
		// User user = dao.fetch(User.class, Cnd.where("id", "=", uid));
		Sql sqlRoleIds = Sqls.create("select role_id from $table $condition");
		sqlRoleIds.vars().set("table", "t_user_role");
		sqlRoleIds.setCondition(Cnd.where("u_id", "=", uid));
		sqlRoleIds.setCallback(new SqlCallback() {
			@Override
			public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
				Set<Integer> roleIds = new HashSet<Integer>();
				while (rs.next()) {
					roleIds.add(rs.getInt(1));
				}
				return roleIds;
			}
		});
		dao.execute(sqlRoleIds);
		Set<Integer> roleIds = sqlRoleIds.getObject(Set.class);
		if (Lang.isEmpty(roleIds)) {
			return;
		}
		Set<Integer> permIdSet = new HashSet();
		for (Integer roleId : roleIds) {
			Sql sqlPermIds = Sqls.create("select permission_id from $table $condition");
			sqlPermIds.vars().set("table", "t_role_permission");
			sqlPermIds.setCondition(Cnd.where("role_id", "=", roleId));
			sqlPermIds.setCallback(new SqlCallback() {
				Set<Integer> permIdSet = new HashSet();

				@Override
				public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
					while (rs.next()) {
						permIdSet.add(rs.getInt(1));
					}
					return permIdSet;
				}
			});
			dao.execute(sqlPermIds);
			permIdSet.addAll(sqlPermIds.getObject(Set.class));
		}
		Set<String> set = new HashSet();
		for (Integer permid : permIdSet) {
			Sql sql = Sqls.create("select name from $table $condition");
			sql.setEntity(dao.getEntity(net.wendal.nutzbook.bean.Permission.class));
			sql.vars().set("table", sql.getEntity().getTableName());
			sql.setCondition(Cnd.where("id", "=", permid));
			sql.setCallback(Sqls.callback.str());
			dao.execute(sql);
			String name = sql.getObject(String.class);
			set.add(name);
		}
		if (Lang.isEmpty(set)) {
			return;
		}
		boolean isRight = false;
		String wildcardString = DirectiveUtils.getString(PERM, params);
		String[] ps = StringUtils.split(wildcardString);
		Collection<org.apache.shiro.authz.Permission> systemPerms = resolvePermissions(ps);
		for (org.apache.shiro.authz.Permission permission : systemPerms) {
			for (String uPerm : set) {
				if (permission.implies(permissionResolver.resolvePermission(uPerm)) || permissionResolver.resolvePermission(uPerm).implies(permission)) {
					isRight = true;
					body.render(env.getOut());
					break;
				}
			}
			if (isRight) {
				break;
			}
		}

	}

	private Collection<org.apache.shiro.authz.Permission> resolvePermissions(String... stringPerms) {
		Collection<String> pe = Lang.array2list(stringPerms, String.class);
		Collection<org.apache.shiro.authz.Permission> perms = PermissionUtils.resolvePermissions(pe, permissionResolver);
		return perms;
	}

	public static void main(String[] args) {
		PermissionResolver permissionResolver = new WildcardPermissionResolver();

		System.out.println(permissionResolver.resolvePermission("*:*:*").implies(permissionResolver.resolvePermission("game.server")));
	}
}
