package net.wendal.nutzbook.mvc;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter2;
import org.nutz.mvc.View;

import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.service.yvr.YvrService;

/**
 * 通过请求参数中的accesstoken进行授权
 * @author wendal
 *
 */
public class AccessTokenFilter implements ActionFilter2 {
	
	private static final Log log = Logs.get();
	
	YvrService yvrService;

	public View match(ActionContext ac) {
		if (yvrService == null)
			yvrService = ac.getIoc().get(YvrService.class);
		HttpServletRequest req = ac.getRequest();
		String at = req.getParameter("accesstoken");
		if (req.getHeader("Api-Version") != null) {
			log.debug("api version = " + req.getHeader("Api-Version"));
			String loginname = req.getHeader("Api-Loginname");
			String nonce = req.getHeader("Api-Nonce");
			String key = req.getHeader("Api-Key");
			String time = req.getHeader("Api-Time");
			if (Strings.isBlank(loginname) || Strings.isBlank(nonce) || Strings.isBlank(key) || Strings.isBlank(time)) {
				return BaseModule.HTTP_403;
			}
			if (!yvrService.checkNonce(nonce, time)){
				return BaseModule.HTTP_403;
			}
			at = yvrService.accessToken(loginname);
			if (Strings.isBlank(at)) {
				return BaseModule.HTTP_403;
			}
			String tmp = Strings.join(",", at, loginname, nonce, time);
			String _key = Lang.sha1(tmp);
			log.debug("tmp="+tmp);
			log.debug("_key=" + _key);
			log.debug(" key=" + key);
			if (!_key.equals(key)) {
				return BaseModule.HTTP_403;
			}
			log.debug("api access token check ok");
		} 
		else if (Strings.isBlank(at)) { // TODO 移除这种兼容性,改成必须用nonce加密
			return BaseModule.HTTP_403;
		}
		int uid = yvrService.getUserByAccessToken(at);
		if (uid < 1) {
			return BaseModule.HTTP_403;
		}
		SecurityUtils.getSubject().runAs(new SimplePrincipalCollection(uid, "nutzdao_realm"));
		return null;
	}

	public void after(ActionContext ctx) {
		if (SecurityUtils.getSubject().isRunAs())
			SecurityUtils.getSubject().releaseRunAs();
	}
}
