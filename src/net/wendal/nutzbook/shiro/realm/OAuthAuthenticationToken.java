package net.wendal.nutzbook.shiro.realm;

import org.apache.shiro.authc.UsernamePasswordToken;

public class OAuthAuthenticationToken extends UsernamePasswordToken {

	private static final long serialVersionUID = 1L;
	
	protected int userId;
	
	public OAuthAuthenticationToken(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
}
