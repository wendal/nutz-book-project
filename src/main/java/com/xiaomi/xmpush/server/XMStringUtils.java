package com.xiaomi.xmpush.server;

public class XMStringUtils {
	public static boolean isEmpty(CharSequence cs) {
		return (cs == null) || (cs.length() == 0);
	}

	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if ((cs == null) || ((strLen = cs.length()) == 0))
			return true;
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String trim(String str) {
		return str == null ? null : str.trim();
	}
}