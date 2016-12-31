package net.wendal.nutzbook.mailsrv;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.nutz.dao.Dao;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.util.blob.SimpleBlob;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.subethamail.smtp.helper.SimpleMessageListener;

import net.wendal.nutzbook.bean.BigContent;
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
            File tmp = Jdbcs.getFilePool().createFile(".mail");
            Files.write(tmp, data);
            BigContent cnt = new BigContent();
            cnt.setData(new SimpleBlob(tmp));


            SmtpMail mail = new SmtpMail();
            mail.setFrom(from);
            mail.setRecipient(recipient);
            try (FileInputStream ins = new FileInputStream(tmp)) {
                MimeMessage msg = new MimeMessage(getSession(), ins);
                log.debug("msg subject = " + msg.getSubject());
                log.debug("msg content-type = " + msg.getContentType());
                log.debug("msg class = " + msg.getClass().getName());
                mail.setSubject(msg.getSubject());
            }
            dao.insert(cnt);
            mail.setContentId(cnt.getId());
            dao.insert(mail);
        }
        catch (Exception e) {
            log.debug("rec mail fail", e);
        }
    }

    /**
     * Creates the JavaMail Session object for use in WiserMessage
     */
    protected Session getSession()
    {
        return Session.getDefaultInstance(new Properties());
    }
}
