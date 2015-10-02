package net.wendal.nutzbook.bean.doodle;

import java.util.List;

import net.wendal.nutzbook.bean.BasePojo;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.random.R;

@Table("t_doodle")
public class Doodle extends BasePojo {
	private static final long serialVersionUID = 7913169799862596329L;

	@Name
	@Prev(els=@EL("$me.uuid()"))
	private String id;
	
	@Column
	@ColDefine(width=128)
	private String title;
	
	@Column("dt")
	@ColDefine(type = ColType.VARCHAR, width = 500)
	private String description;
	
	@Column("loc")
	private String location;
	
	@Column("creator")
	private String creator;
	
	@Column("email")
	private String email;
	
	@Column("atk")
	private String adminToken;
	
	@Many(target=DoodleItem.class, field="doodleId")
	private List<DoodleItem> items;
	
	public String uuid() {
		return R.UU32();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAdminToken() {
		return adminToken;
	}

	public void setAdminToken(String adminToken) {
		this.adminToken = adminToken;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<DoodleItem> getItems() {
		return items;
	}

	public void setItems(List<DoodleItem> items) {
		this.items = items;
	}

	
}
