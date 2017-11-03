package net.wendal.nutzbook.core.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.*;
import org.nutz.json.JsonField;

import net.wendal.nutzbook.common.util.Toolkit;

@Table("t_user_profile")
@Comment("用户基本信息表")
public class UserProfile extends BasePojo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**关联的用户id*/
	@Id(auto=false)
	@Column("u_id")
	@Comment("用户编号")
	protected long userId;
	/**用户昵称*/
	@Column
	@Comment("用户昵称")
	protected String nickname;
	/**用户邮箱*/
	@Column
	@Comment("用户邮箱")
	protected String email;
	/**邮箱是否已经验证过*/
	@Column("email_checked")
	@Comment("邮箱是否已经验证过")
	protected boolean emailChecked;
	/**头像的byte数据*/
	@Column
	@JsonField(ignore=true)
	@Comment("用户头像")
	protected byte[] avatar;
	/**性别*/
	@Column
	@Comment("性别")
	protected String gender;
	/**自我介绍*/
	@Column("dt")
	@Comment("自我介绍")
	protected String description;
	@Column("loc")
	@Comment("地址")
	protected String location;
	
	@JsonField(ignore=true)
	@One(target=User.class, field="userId")
	protected User user;
	
	// 升级为数据库字段
	@Column
	@Comment("登陆名")
	protected String loginname;
	
	// 非数据库字段开始-----------------
	protected int score;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isEmailChecked() {
		return emailChecked;
	}
	public void setEmailChecked(boolean emailChecked) {
		this.emailChecked = emailChecked;
	}
	public byte[] getAvatar() {
		return avatar;
	}
	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	
	public String getCreateAt() {
		return Toolkit.createAt(createTime);
	}
	
	public String getUpdateAt() {
		return Toolkit.createAt(updateTime);
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getDisplayName() {
	    if (nickname.equalsIgnoreCase(loginname))
	        return loginname;
	    return nickname+"("+loginname+")";
	}
}
