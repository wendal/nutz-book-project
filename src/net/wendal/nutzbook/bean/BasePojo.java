package net.wendal.nutzbook.bean;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public abstract class BasePojo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column("ct")
	protected Date createTime;
	@Column("ut")
	protected Date updateTime;
	
	public String toString() {
		return String.format("/*%s*/%s", super.toString(), Json.toJson(this, JsonFormat.compact()));
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
