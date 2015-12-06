package net.wendal.nutzbook.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import net.wendal.nutzbook.bean.Permission;
import net.wendal.nutzbook.bean.Role;

import org.nutz.lang.Lang;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class PermissionDirective implements TemplateDirectiveModel {

	private final static String ERROR_ID = "-1";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Role role = (Role) DirectiveUtils.getObject("role", params);
		String wildcardString = DirectiveUtils.getString("perm", params);
		List<Permission> pList = (List<Permission>) DirectiveUtils.getObject("permList", params);
		Writer out = env.getOut();
		Permission permission = permission(pList, wildcardString);
		boolean isFalse = Lang.isEmpty(permission);
		out.append("<input value=");
		out.append('"');
		if (isFalse) {
			out.append(ERROR_ID);
		} else {
			out.append(String.valueOf(permission.getId()));
		}
		out.append('"');
		out.append("type=");
		out.append('"');
		out.append("checkbox");
		out.append('"');
		if (isFalse) {
			out.append(" disabled=");
			out.append('"');
			out.append("disabled");
			out.append('"');
		}
		boolean isRight = true;
		if (Lang.isEmpty(role)) {
			isRight = false;
		} else {
			isRight = contain(role.getPermissions(), wildcardString);
		}
		if (isRight && !isFalse) {
			out.append(" checked=");
			out.append('"');
			out.append("checked");
			out.append('"');
		}
		out.append(" name=");
		out.append('"');
		out.append("authorities");
		out.append('"');
		out.append("/>");
		out.append(isFalse ? "--" : permission.getDescription());
	}

	private net.wendal.nutzbook.bean.Permission permission(List<Permission> pList, String wildcardString) {
		if (Lang.isEmpty(pList)) {
			return null;
		}
		for (Permission p : pList) {
			String name = p.getName();
			boolean isRight = Lang.equals(wildcardString, name);
			if (isRight) {
				return p;
			}
		}
		return null;
	}

	private boolean contain(List<Permission> pList, String wildcardString) {
		if (Lang.isEmpty(pList)) {
			return false;
		}
		for (Permission p : pList) {
			String name = p.getName();
			boolean isRight = Lang.equals(wildcardString, name);
			if (isRight) {
				return isRight;
			}
		}
		return false;
	}
}
