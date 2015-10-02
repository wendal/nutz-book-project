package net.wendal.nutzbook.bean.yvr;

import java.util.List;

import net.wendal.nutzbook.bean.BasePojo;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.util.Toolkit;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

@Table("t_topic")
public class Topic extends BasePojo {

	private static final long serialVersionUID = -8090885594965549361L;

	@Name
	@Prev(els=@EL("$me.uuid()"))
	protected String id;
	
	@Column
	@ColDefine(width=512)
	protected String title;
	
	@Column("tp")
	protected TopicType type;
	
	@Column("cnt")
	@ColDefine(width=20000)
	protected String content;
	
	@Column
	@ColDefine(width=128)
	protected List<String> tags;
	
	@Column("u_id")
	protected int userId;
	
	@Column("c_top")
	protected boolean top;
	
	@Column("c_good")
	protected boolean good;
	
	@Column("c_lock")
	protected boolean lock;
	
	// 浏览总数
	protected transient long visitCount;
	
	// 回复总数
	protected transient long replyCount;
	
	protected TopicReply lastComment;
	
	@Many(target=TopicReply.class, field = "topicId")
	protected List<TopicReply> replies;
	
	@One(target=UserProfile.class, field="userId")
	protected UserProfile author;
	
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

	public TopicType getType() {
		return type;
	}

	public void setType(TopicType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public List<TopicReply> getReplies() {
		return replies;
	}
	
	public void setReplies(List<TopicReply> replies) {
		this.replies = replies;
	}

	public UserProfile getAuthor() {
		return author;
	}
	
	public void setAuthor(UserProfile author) {
		this.author = author;
	}

	public TopicReply getLastComment() {
		return lastComment;
	}

	public void setLastComment(TopicReply lastComment) {
		this.lastComment = lastComment;
	}
	
	public String getCreateAt() {
		return Toolkit.createAt(createTime);
	}
	
	public String getUpdateAt() {
		return Toolkit.createAt(updateTime);
	}

	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public long getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}

	public long getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(long replyCount) {
		this.replyCount = replyCount;
	}
	
	//--SerializationBuilder
	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
	    java.io.DataOutputStream dos = new java.io.DataOutputStream(out);
	    dos.writeUTF(id);
	    dos.writeUTF(title == null ? "" : title);
	    dos.writeUTF(type == null ? "ask" : type.name());
	    dos.writeUTF(content == null ? "" : content);
	    dos.writeInt(userId);
	    dos.writeBoolean(top);
	    dos.writeBoolean(good);
	    dos.writeBoolean(lock);
	    dos.writeLong(createTime == null ? 0 : createTime.getTime());
	    dos.writeLong(updateTime == null ? 0 : updateTime.getTime());

	}
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
	    java.io.DataInputStream dis = new java.io.DataInputStream(in);
	    String _tmp = null;
	    id = dis.readUTF();
	    title = dis.readUTF();
	    _tmp = dis.readUTF(); if (_tmp != null) type=net.wendal.nutzbook.bean.yvr.TopicType.valueOf(_tmp);
	    content = dis.readUTF();
	    userId = dis.readInt();
	    top = dis.readBoolean();
	    good = dis.readBoolean();
	    lock = dis.readBoolean();
	    createTime = new java.util.Date(dis.readLong());
	    updateTime = new java.util.Date(dis.readLong());

	}
	@SuppressWarnings("unused")
	private void readObjectNoData() throws java.io.ObjectStreamException{}
	//SerializationBuilder--
}
