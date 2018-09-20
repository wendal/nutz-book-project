package net.wendal.nutzbook.yvr.bean;

import java.util.Collections;
import java.util.Set;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.bean.BasePojo;
import net.wendal.nutzbook.core.bean.UserProfile;

@Table("t_topic_reply")
public class TopicReply extends BasePojo {

	private static final long serialVersionUID = 5165667887317040294L;

	@Name
	@Prev(els = @EL("$me.uuid()") )
	protected String id;

	@Column
	protected String topicId;

	@Column
	protected String replyTo;

	@Column("u_id")
	protected long userId;

	@Column("cnt")
	@ColDefine(width = 50)
	protected String content;

	@Column("cid")
	protected String contentId;

	@One(target = UserProfile.class, field = "userId")
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
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

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
}
