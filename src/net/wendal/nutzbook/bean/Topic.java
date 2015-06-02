package net.wendal.nutzbook.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

@Table("t_topic")
public class Topic extends BasePojo {

	private static final long serialVersionUID = 1L;

	@Column
	@Name
	@Prev(els={@EL("uuid()")})
	private String id;

	@Column
	@ColDefine(width=128)
	private String title;

	@Column
	@ColDefine(width=10240)
	private String content;
	
	@Column
	@ColDefine(width=256)
	private String tab;

	@Column
	@ColDefine(width=256)
	private String tags;

	@Column
	private int userId;
	
	@Column("stat")
	private int status;
	
	@One(target=User.class, field="userId")
	private User user;
	
	@Column
	public int vistors;
	
	// 非数据库字段
	
	public int replies;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public int getVistors() {
		return vistors;
	}

	public void setVistors(int vistors) {
		this.vistors = vistors;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}
	
	
}
