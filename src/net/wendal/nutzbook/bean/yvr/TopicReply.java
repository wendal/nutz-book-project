package net.wendal.nutzbook.bean.yvr;

import java.util.Collections;
import java.util.Set;

import net.wendal.nutzbook.bean.BasePojo;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.util.Toolkit;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

@Table("t_topic_reply")
public class TopicReply extends BasePojo {

	private static final long serialVersionUID = 5165667887317040294L;

	@Name
	@Prev(els=@EL("$me.uuid()"))
	protected String id;
	
	@Column
	protected String topicId;
	
	@Column("u_id")
	protected int userId;
	
	@Column("cnt")
	@ColDefine(width=20000)
	protected String content;
	
	@One(target=UserProfile.class, field="userId")
	protected UserProfile author;
	
	@SuppressWarnings("unchecked")
	protected Set<String> ups = Collections.EMPTY_SET;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCreateAt() {
		return Toolkit.createAt(createTime);
	}
	
	public String getUpdateAt() {
		return Toolkit.createAt(updateTime);
	}

	public UserProfile getAuthor() {
		return author;
	}

	public void setAuthor(UserProfile author) {
		this.author = author;
	}

	public Set<String> getUps() {
		return ups;
	}

	public void setUps(Set<String> ups) {
		this.ups = ups;
	}
}
