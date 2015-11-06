package net.wendal.nutzbook.shiro.realm;

import net.wendal.nutzbook.bean.User;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.nutz.lang.Lang;

public class OAuthRealm extends NutDaoRealm {
	
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		OAuthAuthenticationToken cst = (OAuthAuthenticationToken)token;
		User user = dao().fetch(User.class, cst.getUserId());
		if (user == null || user.isLocked())
			throw Lang.impossible(); // 不可能吧
		cst.setUsername(user.getName());
        return new SimpleAccount(user.getId(), user.getPassword(), getName());
	}
	
	public OAuthRealm() {
		setAuthenticationTokenClass(OAuthAuthenticationToken.class);
	}
	
	/**
	 * 覆盖父类的验证,直接pass
	 */
	protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
	}
}
