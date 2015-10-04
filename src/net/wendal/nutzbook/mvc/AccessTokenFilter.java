package net.wendal.nutzbook.mvc;

import javax.servlet.http.HttpSession;

import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.service.yvr.YvrService;

import org.nutz.integration.shiro.NutShiro;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

/**
 * 通过请求参数中的accesstoken进行授权
 * @author wendal
 *
 */
public class AccessTokenFilter implements ActionFilter {
	
	YvrService yvrService;

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
		if (yvrService == null)
			yvrService = ac.getIoc().get(YvrService.class);
		int uid = yvrService.getUserByAccessToken(at);
		if (uid < 1) {
			return BaseModule.HTTP_403;
		}
		session.setAttribute(NutShiro.SessionKey, uid);
		session.setAttribute(NutShiro.SessionKey + "_at", at);
		return null;
	}

}
