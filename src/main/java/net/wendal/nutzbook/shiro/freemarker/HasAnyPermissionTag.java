package net.wendal.nutzbook.shiro.freemarker;

import org.apache.commons.lang.StringUtils;

public class HasAnyPermissionTag extends PermissionTag {
	private static final String ROLE_NAMES_DELIMETER = ",";

	@Override
	protected boolean isPermitted(String p) {
		if (getSubject() == null || StringUtils.isBlank(p)) {
			return false;
		}
		String[] permissionStrs = StringUtils.split(p, ROLE_NAMES_DELIMETER);
		boolean[] haveAnyPermission = getSubject().isPermitted(permissionStrs);
		for (boolean isRight : haveAnyPermission) {
			if (isRight) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean showTagBody(String p) {
		return isPermitted(p);
	}
}
