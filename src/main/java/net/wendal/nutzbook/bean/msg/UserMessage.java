package net.wendal.nutzbook.bean.msg;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableMeta;

import net.wendal.nutzbook.bean.BasePojo;

@Table("t_user_message")
@TableMeta("{mysql-charset:'utf8mb4', 'mysql-engine':'myisam'}")
public class UserMessage extends BasePojo {
    
    private static final long serialVersionUID = -3393085230478066007L;
    
    @Id(auto=false)
    @Prev(els=@EL("ig(view.tableName)"))
    protected int id;
    @Column("sender_id")
    protected int senderId;
    @Column("revc_id")
    protected int receiverId;
    @Column("cnt")
    @ColDefine(width=1024*4)
    protected String content;
    @Column
    protected boolean unread;
    @Column("tip")
    protected String topicId;
    @Column("rip")
    protected String replyId;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public boolean isUnread() {
        return unread;
    }
    public void setUnread(boolean unread) {
        this.unread = unread;
    }
    public String getTopicId() {
        return topicId;
    }
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getReplyId() {
        return replyId;
    }
    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }
    public int getSenderId() {
        return senderId;
    }
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    public int getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
    
    
}
