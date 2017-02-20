package net.wendal.nutzbook.core.service.impl;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.Callback;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.wendal.nutzbook.core.service.EmailService;

@IocBean(name = "emailService")
public class EmailServiceImpl implements EmailService {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    public boolean send(String to, String subject, String html) throws EmailException {
        HtmlEmail email = new EmailFactory().make(conf);
        email.setSubject(subject);
        email.setHtmlMsg(html);
        email.addTo(to);
        email.buildMimeMessage();
        email.sendMimeMessage();
        return true;
    }

    @Async
    public void sendAsync(String to, String subject, String html, Callback<Boolean> callback) {
        boolean re = false;
        try {
            re = this.send(to, subject, html);
        }
        catch (EmailException e) {
            log.debug("send fail", e);
        }
        if (callback != null)
            callback.invoke(re);
    }
}
