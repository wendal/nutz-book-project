package net.wendal.nutzbook.core.service.impl;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;

public class EmailFactory {

    public ImageHtmlEmail make(PropertiesProxy conf) throws EmailException {
        DefaultAuthenticator auth = new DefaultAuthenticator(conf.get("mail.username"), conf.get("mail.password"));
        ImageHtmlEmail mail = new ImageHtmlEmail();
        mail.setAuthenticator(auth);
        mail.setHostName(conf.get("mail.host"));
        mail.setSSLOnConnect(conf.getBoolean("mail.ssl", true));
        if (mail.isSSLOnConnect()) {
            mail.setSmtpPort(conf.getInt("mail.port", 465));
        } else {
            mail.setSmtpPort(conf.getInt("mail.port", 25));
        }
        mail.setCharset(conf.get("mail.charset", Encoding.UTF8));
        mail.setFrom(conf.get("mail.from"));
        return mail;
    }
}
