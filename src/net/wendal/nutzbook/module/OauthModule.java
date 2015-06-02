package net.wendal.nutzbook.module;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.shiro.realm.OAuthAuthenticationToken;

import org.apache.shiro.SecurityUtils;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.exception.SocialAuthException;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.random.R;
import org.nutz.lang.stream.NullInputStream;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean(create = "init")
@At("/oauth")
public class OauthModule extends BaseModule {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected UserService userService;

	@At("/?")
	public void auth(String provider, HttpSession session,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String returnTo = req.getRequestURL().toString() + "/callback";
		if (req.getParameterMap().size() > 0) {
			StringBuilder sb = new StringBuilder().append(returnTo).append("?");
			for (Object name : req.getParameterMap().keySet()) {
				sb.append(name)
						.append('=')
						.append(URLEncoder.encode(
								req.getParameter(name.toString()),
								Encoding.UTF8)).append("&");
			}
			returnTo = sb.toString();
		}
		SocialAuthManager manager = new SocialAuthManager(); // 每次都要新建哦
		manager.setSocialAuthConfig(config);
		String url = manager.getAuthenticationUrl(provider, returnTo);
		log.info("URL=" + url);
		Mvcs.getResp().setHeader("Location", url);
		Mvcs.getResp().setStatus(302);
		session.setAttribute("openid_manager", manager);
	}

	@At("/?/callback")
	@Ok(">>:/ask")
	public void callback(String _provider, HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SocialAuthManager manager = (SocialAuthManager) session.getAttribute("openid_manager");
		if (manager == null)
			throw new SocialAuthException("Not manager found!");
		session.removeAttribute("openid_manager"); //防止重复登录的可能性
		Map<String, String> paramsMap = SocialAuthUtil.getRequestParametersMap(req); 
		AuthProvider provider = manager.connect(paramsMap);
		Profile p = provider.getUserProfile();
		
		// TODO 关联用户
		String userName = provider.getProviderId() + "_" + p.getValidatedId();
		User user = dao.fetch(User.class, userName);
		if (user == null) {
			user = userService.add(userName, R.UU32() + "@" + p.getProviderId());
		}
		UserProfile profile = dao.fetch(UserProfile.class, user.getId());
		if (profile == null) {
			profile = new UserProfile();
			profile.setUserId(user.getId());
			profile.setNickname(p.getDisplayName());
			profile.setLocation(p.getLocation());
			if (p.getEmail() != null) {
				profile.setEmail(p.getEmail());
				profile.setEmailChecked(true);
			}
			profile.setCreateTime(new Date());
			profile.setUpdateTime(profile.getCreateTime());
			
			dao.insert(profile);
		}
		// 进行Shiro登录
		SecurityUtils.getSubject().login(new OAuthAuthenticationToken(user.getId()));
		session.setAttribute("me", user.getId());
	}

	private SocialAuthConfig config;

	public void init() throws Exception {
		SocialAuthConfig config = new SocialAuthConfig();
		File devConfig = Files.findFile("oauth_consumer.properties_dev"); // 开发期所使用的配置文件
		if (devConfig == null)
			devConfig = Files.findFile("oauth_consumer.properties"); // 真实环境所使用的配置文件
		if (devConfig == null)
			config.load(new NullInputStream());
		else
			config.load(new FileInputStream(devConfig));
		this.config = config;
	}
}
