package net.wendal.nutzbook.core.bean;

import javax.servlet.http.HttpServletRequest;

import org.nutz.mvc.Mvcs;
/**
 * @author 科技㊣²º¹³
 * 2014年2月3日 下午4:48:45
 * http://www.rekoe.com
 * QQ:5382211
 */
public class Message {

	private Type type;
	private String content;

	public enum Type {
		success, warn, error;
	}

	public Message() {
	}

	public Message(Message.Type type, String content,HttpServletRequest req) {
		this.type = type;
		this.content = Mvcs.getMessage(req, content);
	}

	public static Message success(String content,HttpServletRequest req) {
		return new Message(Message.Type.success, content,req);
	}

	public static Message warn(String content,HttpServletRequest req) {
		return new Message(Message.Type.warn, content, req);
	}

	public static Message error(String content,HttpServletRequest req) {
		return new Message(Message.Type.error, content, req);
	}

	public Message.Type getType() {
		return this.type;
	}

	public void setType(Message.Type type) {
		this.type = type;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
