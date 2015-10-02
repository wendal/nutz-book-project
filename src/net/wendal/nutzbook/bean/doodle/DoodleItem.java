package net.wendal.nutzbook.bean.doodle;

import net.wendal.nutzbook.bean.BasePojo;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.random.R;

@Table("t_doodle_item")
public class DoodleItem extends BasePojo {
	private static final long serialVersionUID = 365501471080114064L;

	@Name
	@Prev(els=@EL("$me.uuid()"))
	private String id;
	
	@Column("did")
	private String doodleId;
	
	@Column("nm")
	private String name;
	
	@Column("co")
	private String cookie;
	

	public String uuid() {
		return R.UU32();
	}

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

	public String getDoodleId() {
		return doodleId;
	}

	public void setDoodleId(String doodleId) {
		this.doodleId = doodleId;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
}
