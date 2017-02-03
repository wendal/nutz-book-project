package net.wendal.nutzbook.core.bean;

import java.io.Serializable;

public class CResult implements Serializable {
	
	private static final long serialVersionUID = 5491517871007079209L;
	
	private boolean ok;
	protected boolean success;
	private String msg;
	private Object data;
	
	public boolean isOk() {
		return ok;
	}
	public CResult setOk(boolean ok) {
		this.ok = ok;
		this.success = ok;
		return this;
	}
	public boolean isSuccess() {
		return ok;
	}
	public CResult setSuccess(boolean success) {
		this.ok = success;
		this.success = success;
		return this;
	}
	public String getMsg() {
		return msg;
	}
	public CResult setMsg(String msg) {
		this.msg = msg;
		return this;
	}
	public Object getData() {
		return data;
	}
	public CResult setData(Object data) {
		this.data = data;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T as(Class<T> klass) {
		return (T)this.data;
	}
	
	public static CResult _ok(Object data) {
		return new CResult().setOk(true).setData(data);
	}
	public static CResult _fail(String msg) {
		return new CResult().setOk(false).setMsg(msg);
	}
}
