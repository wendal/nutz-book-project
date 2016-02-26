package com.xiaomi.xmpush.server;

public enum PushJobType {
	Invalid(0), Topic(1), Common(2), Alias(3), BatchAlias(4), BatchRegId(5), ALL(6), UserAccount(7), BatchUserAccount(8);

	private final byte value;
	private static PushJobType[] VALID_JOB_TYPES = { Topic, Common, Alias, UserAccount };

	private PushJobType(int value) {
		this((byte) value);
	}

	private PushJobType(byte value) {
		this.value = value;
	}

	public byte value() {
		return this.value;
	}

	public static PushJobType from(byte value) {
		for (PushJobType type : VALID_JOB_TYPES) {
			if (type.value == value) {
				return type;
			}
		}
		return Invalid;
	}
}