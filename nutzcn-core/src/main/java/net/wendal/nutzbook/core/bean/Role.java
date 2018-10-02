package net.wendal.nutzbook.core.bean;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.*;

@Table("t_role")
@Comment("角色表")
public class Role extends IdentityPojo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Name
	protected String name;
	@Column("al")
	protected String alias;
	@Column("dt")
	@ColDefine(type = ColType.VARCHAR, width = 500)
	private String description;
	@ManyMany(from="role_id", relation="t_role_permission", target=Permission.class, to="permission_id")
	protected List<Permission> permissions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
