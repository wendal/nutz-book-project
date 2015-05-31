package net.wendal.nutzbook.crossscreen;

import org.apache.shiro.authc.UsernamePasswordToken;

public class CrossScreenUserToken extends UsernamePasswordToken {

	private static final long serialVersionUID = 1L;
	
	protected int userId;
	
	public CrossScreenUserToken(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
}
