package net.wendal.nutzbook.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.service.yvr.YvrService;

import org.nutz.integration.shiro.NutShiro;
import org.nutz.lang.Lang;
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
		if (yvrService == null)
			yvrService = ac.getIoc().get(YvrService.class);
		HttpServletRequest req = ac.getRequest();
		String at = req.getParameter("accesstoken");
		if (req.getHeader("Api-Version") != null) {
			String loginname = req.getHeader("Api-Loginname");
			String nonce = req.getHeader("Api-Nonce");
			String key = req.getHeader("Api-Key");
			if (Strings.isBlank(loginname) || Strings.isBlank(nonce) || Strings.isBlank(key)) {
				return BaseModule.HTTP_403;
			}
			if (!yvrService.checkNonce(nonce)){
				return BaseModule.HTTP_403;
			}
			at = yvrService.accessToken(loginname);
			if (Strings.isBlank(at)) {
				return BaseModule.HTTP_403;
			}
			String _key = Lang.sha1(Strings.join(",", at, loginname, nonce));
			if (!_key.equals(key)) {
				return BaseModule.HTTP_403;
			}
		} 
		else if (Strings.isBlank(at)) { // TODO 移除这种兼容性,改成必须用nonce加密
			return BaseModule.HTTP_403;
		}
		HttpSession session = req.getSession();
		if (session.getAttribute(NutShiro.SessionKey + "_at") != null) {
			String tmp = (String) session.getAttribute(NutShiro.SessionKey + "_at");
			if (tmp.equals(at) && session.getAttribute(NutShiro.SessionKey) != null) {
				return null;
			}
		}
		int uid = yvrService.getUserByAccessToken(at);
		if (uid < 1) {
			return BaseModule.HTTP_403;
		}
		session.setAttribute(NutShiro.SessionKey, uid);
		session.setAttribute(NutShiro.SessionKey + "_at", at);
		return null;
	}

}
