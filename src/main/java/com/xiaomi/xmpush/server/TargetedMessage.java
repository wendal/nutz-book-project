package com.xiaomi.xmpush.server;

public class TargetedMessage {
	private Message message;

	private int targetType;

	private String target;

	public static final int TARGET_TYPE_REGID = 1;

	public static final int TARGET_TYPE_ALIAS = 2;

	public static final int TARGET_TYPE_USER_ACCOUNT = 3;

	public TargetedMessage setTarget(int targetType, String target) {
		this.targetType = targetType;
		this.target = target;
		return this;
	}

	public TargetedMessage setMessage(Message message) {
		this.message = message;
		return this;
	}

	public Message getMessage() {
		return this.message;
	}

	public int getTargetType() {
		return this.targetType;
	}

	public String getTarget() {
		return this.target;
	}
}