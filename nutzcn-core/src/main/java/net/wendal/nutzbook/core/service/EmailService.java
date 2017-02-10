package net.wendal.nutzbook.core.service;

public interface EmailService {

	boolean send(String to, String subject, String html);

}
