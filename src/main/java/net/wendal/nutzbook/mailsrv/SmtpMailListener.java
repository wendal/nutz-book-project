package net.wendal.nutzbook.mailsrv;

import java.io.InputStream;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.subethamail.smtp.helper.SimpleMessageListener;

import net.wendal.nutzbook.bean.mainsrv.SmtpMail;

@IocBean
public class SmtpMailListener implements SimpleMessageListener {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected Dao dao;

    public boolean accept(String from, String recipient) {
        return true;
    }

    public void deliver(String from, String recipient, InputStream data) {
        try {
            log.debugf("from<%s> to<%s>", from, recipient);
            SmtpMail mail = new SmtpMail();
            mail.setFrom(from);
            mail.setRecipient(recipient);
            mail.setContent(data);
            dao.insert(mail);
        }
        catch (Exception e) {
            log.debug("rec mail fail", e);
        }
    }

}
