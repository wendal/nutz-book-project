package net.wendal.nutzbook.core.bean;

import java.io.Serializable;
import java.sql.Blob;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableMeta;

/**
 * 专门存放大数据,当KV数据库用
 * @author wendal
 *
 */
@Table("t_big_content")
@TableMeta("{'mysql-charset':'utf8mb4'}")
public class BigContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
	protected String id;
	
	@Column("dt")
	protected Blob data;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}
	
}
