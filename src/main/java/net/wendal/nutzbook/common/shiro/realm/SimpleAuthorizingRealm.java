package net.wendal.nutzbook.common.shiro.realm;

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
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.integration.shiro.AbstractSimpleAuthorizingRealm;
import org.nutz.integration.shiro.SimpleShiroToken;

import net.wendal.nutzbook.core.bean.Permission;
import net.wendal.nutzbook.core.bean.Role;
import net.wendal.nutzbook.core.bean.User;

public class SimpleAuthorizingRealm extends AbstractSimpleAuthorizingRealm {

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// null usernames are invalid
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		long userId = ((Number) principals.getPrimaryPrincipal()).longValue();
		User user = dao().fetch(User.class, userId);
		if (user == null)
			return null;
		if (user.isLocked())
			throw new LockedAccountException("Account [" + user.getName() + "] is locked.");

		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
        user = dao().fetchLinks(user, null);
        if (user.getRoles() != null) {
            dao().fetchLinks(user.getRoles(), null);
            for (Role role : user.getRoles()) {
                auth.addRole(role.getName());
                // 管理员有全部权限
                if ("admin".equals(role.getName())) {
                    for (Permission p : dao().query(Permission.class, null)) {
                        auth.addStringPermission(p.getName());
                    }
                    break;
                }
                if (role.getPermissions() != null) {
                    for (Permission p : role.getPermissions()) {
                        auth.addStringPermission(p.getName());
                    }
                }
            }
        }
        if (user.getPermissions() != null) {
            for (Permission p : user.getPermissions()) {
                auth.addStringPermission(p.getName());
            }
        }
        return auth;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		SimpleShiroToken upToken = (SimpleShiroToken) token;

		User user = dao().fetch(User.class, (Long)upToken.getPrincipal());
		if (user == null)
			return null;
		if (user.isLocked())
			throw new LockedAccountException("Account [" + user.getName() + "] is locked.");
		return new SimpleAccount(user.getId(), user.getPassword(), getName());
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
	
}
