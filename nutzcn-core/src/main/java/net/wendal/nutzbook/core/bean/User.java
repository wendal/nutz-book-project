package net.wendal.nutzbook.core.bean;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.*;

@Table("t_user")
@Comment("用户表")
public class User extends BasePojo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id(auto=false)
	@Prev(els=@EL("ig(view.tableName)"))
	@Comment("用户编号")
	protected long id;
	@Name
	@Column
	@Comment("用户名")
	protected String name;
	@Column("passwd")
	@ColDefine(width=128)
	@Comment("用户密码")
	protected String password;
	@Column
	@Comment("盐")
	protected String salt;
	@Column
	@Comment("是否锁定")
	protected boolean locked;
	@ManyMany(from="u_id", relation="t_user_role", target=Role.class, to="role_id")
	protected List<Role> roles;
	@ManyMany(from="u_id", relation="t_user_permission", target=Permission.class, to="permission_id")
	protected List<Permission> permissions;
	@One(target=UserProfile.class, field="id", key="userId")
	protected UserProfile profile;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}

	
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	public UserProfile getProfile() {
		return profile;
	}
	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}
}
