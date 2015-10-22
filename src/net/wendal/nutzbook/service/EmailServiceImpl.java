package net.wendal.nutzbook.service;

import java.io.IOException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.nutz.integration.zbus.annotation.ZBusConsumer;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;

@ZBusConsumer(mq="email")
@IocBean(name="emailService")
public class EmailServiceImpl implements EmailService, MessageHandler {

	private static final Log log = Logs.get();
	
	@Inject("refer:$ioc")
	protected Ioc ioc;

	public boolean send(String to, String subject, String html) {
		try {
			HtmlEmail email = ioc.get(HtmlEmail.class);
			email.setSubject(subject);
			email.setHtmlMsg(html);
			email.addTo(to);
			email.buildMimeMessage();
			email.sendMimeMessage();
			return true;
		} catch (Throwable e) {
			log.info("send email fail", e);
			return false;
		}
	}

	public boolean isSupport(Object event) {
		return event instanceof Email;
	}
	
	public void handle(Message msg, Session sess) throws IOException {
		Email email = Json.fromJson(Email.class, msg.getBodyString());
		try {
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
}
