package net.wendal.nutzbook.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

/**
 * OAuth授权与系统内User的映射
 * @author wendal
 *
 */
@Table("t_oauth_user")
@PK(value={"providerId", "validatedId"})
public class OAuthUser extends BasePojo {

	private static final long serialVersionUID = 1L;

	/**
	 * 提供者,例如github
	 */
	@Column("pvd")
	private String providerId;
	
	@Column("vid")
	private String validatedId;
	
	/**
	 * 需要映射的系统内User
	 */
	@Column("u_id")
	private int userId;
	
	public OAuthUser() {
	}

	public OAuthUser(String providerId, String validatedId, int userId) {
		super();
		this.providerId = providerId;
		this.validatedId = validatedId;
		this.userId = userId;
	}




	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getValidatedId() {
		return validatedId;
	}

	public void setValidatedId(String validatedId) {
		this.validatedId = validatedId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
