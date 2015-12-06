package net.wendal.nutzbook.bean;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author 科技㊣²º¹³<br />
 *         2015年11月30日 上午8:54:23<br />
 *         http://www.rekoe.com<br />
 *         QQ:5382211
 */
@Table("t_permission_category")
public class PermissionCategory implements Serializable {

	private static final long serialVersionUID = 7685127380108984960L;
	@Name
	@Prev(els = { @EL("uuid()") })
	private String id;
	@Column
	private String name;
	@Many(target = Permission.class, field = "permissionCategoryId")
	private List<Permission> permissions;
	@Column("is_locked")
	@ColDefine(type = ColType.BOOLEAN)
	private boolean locked;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
