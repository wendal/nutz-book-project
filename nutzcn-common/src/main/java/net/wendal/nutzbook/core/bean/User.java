package net.wendal.nutzbook.core.bean;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("t_user")
public class User extends IdentityPojo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Name
	@Column
	protected String name;
	@Column("passwd")
	@ColDefine(width=128)
	protected String password;
	@Column
	protected String salt;
	@Column
	protected boolean locked;
	@Column
	@ColDefine(width=4096)
	protected String roleNames;
    @Column
    @ColDefine(width=4096)
	protected String permissionNames;
	protected List<Role> roles;
	protected List<Permission> permissions;
	@One(target=UserProfile.class, field="id", key="userId")
	protected UserProfile profile;
	
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
    public String getRoleNames() {
        return roleNames;
    }
    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }
    public String getPermissionNames() {
        return permissionNames;
    }
    public void setPermissionNames(String permissionNames) {
        this.permissionNames = permissionNames;
    }
}
