package net.wendal.nutzbook.core.module;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.slog.annotation.Slog;

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.bean.UserProfile;

@Api(name="用户管理", description="传说中的增删改查")
@IocBean // 声明为Ioc容器中的一个Bean
@At("/user") // 整个模块的路径前缀
@Ok("json:{locked:'password|salt',ignoreNull:true}") // 忽略password和salt属性,忽略空属性的json输出
@Fail("http:500") // 抛出异常的话,就走500页面
@Slog(tag="用户管理")
public class UserModule extends BaseModule {
	
	@POST
	@Ok("json")
	@At
	@Slog(tag="用户登录", after="用户[${username}] ok=${re.ok}")
	public Object login(@Param("username")String username, 
					  @Param("password")String password,
					  @Param("rememberMe")boolean rememberMe,
					  @Param("captcha")String captcha) {
		NutMap re = new NutMap().setv("ok", false);
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated())
		    return re.setv("ok", true);
		// 看看有无填写验证码
		if (Strings.isBlank(captcha) && !"guest".equals(username)) {
			return re.setv("msg", "必须填写验证码");
		}
		if (Strings.isBlank(username)) {
			return re.setv("msg", "必须填写用户名");
		}
		if (Strings.isBlank(password)) {
			return re.setv("msg", "必须填写密码");
		}
		// session是否有效
		HttpSession session = Mvcs.getHttpSession(false);
		if (session == null) {
			return re.setv("msg", "session已过期");
		}
		// 比对验证码
		String _captcha = (String) session.getAttribute(Toolkit.captcha_attr);
		if (!"guest".equals(username) && Strings.isBlank(_captcha) && !_captcha.equalsIgnoreCase(captcha)) {
			return re.setv("msg", "验证码错误");
		}
		// 检查用户名密码
		User user = dao.fetch(User.class, username);
		if (user == null) {
			return re.setv("msg", "用户不存在"); // TODO: 改成 用户名/密码不正确
		}
		// 比对密码
		if (!userService.checkPassword(user, password)) {
			return re.setv("msg", "密码错误");
		}
		Toolkit.doLogin(new SimpleShiroToken(user.getId()), user.getId());
		subject.getSession().setAttribute("me", user);
		return re.setv("ok", true);
	}
	
	@Ok("raw:jpg")
	@At("/avatar/me")
	@RequiresAuthentication
	public byte[] userAvatar() {
	    byte[] buf = null;
	    UserProfile profile = dao.fetch(UserProfile.class, Toolkit.uid());
	    if (profile == null || profile.getAvatar() == null) {
	        buf = Streams.readBytesAndClose(getClass().getClassLoader().getResourceAsStream("/assets/adminlte/image/user_none.jpg"));
	    } else {
	        buf = profile.getAvatar();
	    }
	    return buf;
	}
	
	@GET
	@Ok("json:full")
    @At("/profile")
    @RequiresAuthentication
    public Object getProfile() {
        return ajaxOk(userService.getUserProfile(Toolkit.uid(), false));
    }
	
	@AdaptBy(type=JsonAdaptor.class)
	@POST
	@Ok("json:full")
    @At("/profile")
    @RequiresAuthentication
    public Object updateProfile(@Param("..")UserProfile profile) {
        profile.setUserId(Toolkit.uid());
        profile.setLoginname(null);
        profile.setCreateTime(null);
        profile.setUpdateTime(new Date());
        dao.updateIgnoreNull(profile);
        return ajaxOk("");
    }
}
