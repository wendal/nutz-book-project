package net.wendal.nutzbook.bean;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

/**
 * 共享字段,免得每个Pojo类都加创建时间和生成时间
 * @author wendal
 *
 */
public abstract class BasePojo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Prev(els=@EL("$me.now()"))
	@Column("ct")
	protected Date createTime;
	@Prev(els=@EL("$me.now()"))
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
	
	public Date now() {
		return new Date();
	}
}
