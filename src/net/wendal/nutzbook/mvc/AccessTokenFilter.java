package net.wendal.nutzbook.mvc;

import javax.servlet.http.HttpSession;

import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.service.yvr.YvrService;

import org.nutz.integration.shiro.NutShiro;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

public class AccessTokenFilter implements ActionFilter {

	public View match(ActionContext ac) {
		String at = ac.getRequest().getParameter("accesstoken");
		if (Strings.isBlank(at)) {
			return BaseModule.HTTP_403;
		}
		HttpSession session = ac.getRequest().getSession();
		if (session.getAttribute(NutShiro.SessionKey + "_at") != null) {
			String tmp = (String) session.getAttribute(NutShiro.SessionKey + "_at");
			if (tmp.equals(at) && session.getAttribute(NutShiro.SessionKey) != null) {
				return null;
			}
		}
		int uid = ac.getIoc().get(YvrService.class).getUserByAccessToken(at);
		if (uid < 1) {
			return BaseModule.HTTP_403;
		}
		ac.getRequest().getSession().setAttribute(NutShiro.SessionKey, uid);
		ac.getRequest().getSession().setAttribute(NutShiro.SessionKey + "_at", at);
		return null;
	}

}
