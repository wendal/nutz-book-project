package net.wendal.nutzbook.shiro.realm;

import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

public class SimpleShiroToken implements HostAuthenticationToken, RememberMeAuthenticationToken{

	private static final long serialVersionUID = -1L;

	protected int userId;
	
	protected boolean rememberMe;
	
	protected String host;
	
	public Object getPrincipal() {
		return userId;
	}

	public Object getCredentials() {
		return null;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public SimpleShiroToken() {
	}

	public SimpleShiroToken(int userId) {
		this.userId = userId;
	}

	public SimpleShiroToken(int userId, boolean rememberMe, String host) {
		this.userId = userId;
		this.rememberMe = rememberMe;
		this.host = host;
	}
}
