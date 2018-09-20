package net.wendal.nutzbook.core.service;

import org.apache.commons.mail.EmailException;
import org.nutz.lang.util.Callback;

public interface EmailService {

	boolean send(String to, String subject, String html) throws EmailException;
	
	void sendAsync(String to, String subject, String html, Callback<Boolean> callback);

}
