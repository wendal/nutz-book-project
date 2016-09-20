package net.wendal.nutzbook.module.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.ServerRedirectView;
import org.nutz.mvc.view.ViewWrapper;
import org.nutz.plugins.view.freemarker.FreeMarkerConfigurer;
import org.nutz.plugins.view.freemarker.FreemarkerView;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.util.Toolkit;

/**
 * @author 科技㊣²º¹³<br />
 *         2015年11月23日 下午14:18:45 <br />
 *         http://www.rekoe.com QQ:5382211
 */
@IocBean(create = "init")
@At("/admin")
public class AdminLoginModule extends BaseModule {

	private static final String TEMPLATE_LOGIN = "templates/front/login/index";

	@Inject
	protected FreeMarkerConfigurer freeMarkerConfigurer;

	@At
	@Filters(@By(type = AuthenticationFilter.class))
	public View login(@Attr("loginToken") UsernamePasswordToken token, HttpSession session, HttpServletRequest req) {
		try {
			User user = dao.fetch(User.class, token.getUsername());
			if (user != null && userService.checkPassword(user, new String(token.getPassword()))) {
				Toolkit.doLogin(new SimpleShiroToken(user.getId()), user.getId());
				return new ServerRedirectView("/admin/main.rk");
			}
		} catch (LockedAccountException e) {
			return new ViewWrapper(new FreemarkerView(freeMarkerConfigurer, TEMPLATE_LOGIN), e.getMessage());
		} catch (AuthenticationException e) {
		} catch (Exception e) {
			return new ViewWrapper(new FreemarkerView(freeMarkerConfigurer, TEMPLATE_LOGIN), e.getMessage());
		}
		return new ViewWrapper(new FreemarkerView(freeMarkerConfigurer, TEMPLATE_LOGIN), Mvcs.getMessage(req, "common.error.login.account"));
	}

	@At
	@Ok(">>:/admin/index.rk")
	public void logout() {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
	}

	@At({"/", "/index"})
	@Ok("fm:templates.front.login.index")
	public Object index() {
		if (SecurityUtils.getSubject().isAuthenticated())
			return new ServerRedirectView("/admin/main.rk");
		return null;
	}
}
