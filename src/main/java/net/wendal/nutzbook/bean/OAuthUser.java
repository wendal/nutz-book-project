package net.wendal.nutzbook.bean;

import org.nutz.dao.entity.annotation.ColDefine;
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
	
	@Column("a_url")
	@ColDefine(width=8192)
	private String avatar_url;
	
	public OAuthUser() {
	}

	public OAuthUser(String providerId, String validatedId, int userId, String avatar_url) {
		super();
		this.providerId = providerId;
		this.validatedId = validatedId;
		this.userId = userId;
		this.avatar_url = avatar_url;
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

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}
//--SerializationBuilder
private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    java.io.DataOutputStream dos = new java.io.DataOutputStream(out);
    dos.writeUTF(providerId==null?"":providerId);
    dos.writeUTF(validatedId==null?"":validatedId);
    dos.writeInt(userId);
    dos.writeUTF(avatar_url==null?"":avatar_url);
    dos.writeLong(createTime == null ? 0 : createTime.getTime());
    dos.writeLong(updateTime == null ? 0 : updateTime.getTime());

}
private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
    java.io.DataInputStream dis = new java.io.DataInputStream(in);
    providerId = dis.readUTF();
    validatedId = dis.readUTF();
    userId = dis.readInt();
    avatar_url = dis.readUTF();
    createTime = new java.util.Date(dis.readLong());
    updateTime = new java.util.Date(dis.readLong());

}
//SerializationBuilder--
}
