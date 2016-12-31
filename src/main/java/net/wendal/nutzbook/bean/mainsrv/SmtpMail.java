package net.wendal.nutzbook.bean.mainsrv;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

import net.wendal.nutzbook.bean.BasePojo;

@Table("t_smtp_mail")
public class SmtpMail extends BasePojo {

    private static final long serialVersionUID = 194493276844752562L;
    
    @Name
    @Prev(els=@EL("uuid()"))
    private String id;
    
    @ColDefine(width=256)
    @Column("frm")
    protected String from;
    @ColDefine(width=256)
    @Column("rec")
    protected String recipient;
    
    @ColDefine(width=1024)
    @Column("sj")
    protected String subject;
    
    @Column("cid")
    protected String contentId;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    
}
