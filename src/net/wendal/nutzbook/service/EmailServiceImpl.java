package net.wendal.nutzbook.service;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.zbus.MsgBus;
import org.nutz.plugins.zbus.MsgEventHandler;

@IocBean(name="emailService")
public class EmailServiceImpl implements EmailService, MsgEventHandler<Email> {

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

	public Object call(MsgBus bus, Email email) throws Exception {
		email.sendMimeMessage();
		return null;
	}
}
