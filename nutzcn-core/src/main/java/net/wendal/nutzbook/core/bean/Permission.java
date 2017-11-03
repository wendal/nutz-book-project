package net.wendal.nutzbook.core.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.*;

@Table("t_permission")
@Comment("权限表")
public class Permission extends IdentityPojo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Name
	protected String name;
	@Column("al")
	protected String alias;
	@Column("dt")
	@ColDefine(type = ColType.VARCHAR, width = 500)
	private String description;

	@Column("permission_category_id")
	private String permissionCategoryId;

	@One(target = PermissionCategory.class, field = "permissionCategoryId")
	private PermissionCategory permissionCategory;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getPermissionCategoryId() {
		return permissionCategoryId;
	}

	public void setPermissionCategoryId(String permissionCategoryId) {
		this.permissionCategoryId = permissionCategoryId;
	}

	public PermissionCategory getPermissionCategory() {
		return permissionCategory;
	}

	public void setPermissionCategory(PermissionCategory permissionCategory) {
		this.permissionCategory = permissionCategory;
	}
}
