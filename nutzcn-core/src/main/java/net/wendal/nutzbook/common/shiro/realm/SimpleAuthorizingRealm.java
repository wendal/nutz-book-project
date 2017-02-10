package net.wendal.nutzbook.common.shiro.realm;

import java.util.Arrays;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.mvc.Mvcs;

import net.wendal.nutzbook.core.bean.Permission;
import net.wendal.nutzbook.core.bean.User;

public class SimpleAuthorizingRealm extends AuthorizingRealm {
	
	protected Dao dao;

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// null usernames are invalid
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		long userId = (Long) principals.getPrimaryPrincipal();
		User user = dao().fetch(User.class, userId);
		if (user == null)
			return null;
		if (user.isLocked())
			throw new LockedAccountException("Account [" + user.getName() + "] is locked.");

		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		if (user.getRoleNames() != null) {
		    boolean flag = false;
		    for (String role : user.getRoleNames().split(",")) {
                auth.addRole(role);
                // 管理员拥有所有权限
                if ("admin".equals(role)) {
                    List<Permission> permissions = Daos.ext(dao(), FieldFilter.create(Permission.class, "name")).query(Permission.class, null);
                    for (Permission permission : permissions) {
                        auth.addStringPermission(permission.getName());
                    }
                    flag = true;
                }
            }
		    if (flag)
		        return auth;
		}
		if (user.getPermissionNames() != null) {
		    auth.addStringPermissions(Arrays.asList(user.getPermissionNames().split(",")));
		}
		return auth;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		SimpleShiroToken upToken = (SimpleShiroToken) token;

		User user = dao().fetch(User.class, (Long)upToken.getPrincipal());
		if (user == null)
			return null;
		if (user.isLocked())
			throw new LockedAccountException("Account [" + user.getName() + "] is locked.");
		return new SimpleAccount(user.getId(), user.getPassword(), getName());
	}
	
	/**
	 * 覆盖父类的验证,直接pass
	 */
	protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
	}

	public SimpleAuthorizingRealm() {
		this(null, null);
	}

	public SimpleAuthorizingRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
		super(cacheManager, matcher);
		setAuthenticationTokenClass(SimpleShiroToken.class);
	}

	public SimpleAuthorizingRealm(CacheManager cacheManager) {
		this(cacheManager, null);
	}

	public SimpleAuthorizingRealm(CredentialsMatcher matcher) {
		this(null, matcher);
	}

	public Dao dao() {
		if (dao == null) {
			dao = Mvcs.ctx().getDefaultIoc().get(Dao.class, "dao");
			return dao;
		}
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

}
