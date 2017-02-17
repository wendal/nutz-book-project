package net.wendal.nutzbook.core.service;

import org.apache.commons.mail.EmailException;

public interface EmailService {

	boolean send(String to, String subject, String html) throws EmailException;

}
