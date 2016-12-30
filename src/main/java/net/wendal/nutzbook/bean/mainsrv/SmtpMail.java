package net.wendal.nutzbook.bean.mainsrv;

import java.io.InputStream;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import net.wendal.nutzbook.bean.BasePojo;

@Table("t_smtp_mail")
public class SmtpMail extends BasePojo {

    private static final long serialVersionUID = 194493276844752562L;
    
    @ColDefine(width=256)
    @Column("frm")
    protected String from;
    @ColDefine(width=256)
    @Column("rec")
    protected String recipient;
    
    @Column("cnt")
    protected InputStream content;

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

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }
    
}
