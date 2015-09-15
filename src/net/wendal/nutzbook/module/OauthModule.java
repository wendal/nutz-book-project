package net.wendal.nutzbook.module;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.wendal.nutzbook.bean.OAuthUser;
import net.wendal.nutzbook.bean.SysLog;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.service.syslog.SysLogService;
import net.wendal.nutzbook.shiro.realm.OAuthAuthenticationToken;
import net.wendal.nutzbook.util.Toolkit;

import org.apache.shiro.SecurityUtils;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.exception.SocialAuthException;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.nutz.integration.shiro.NutShiro;
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
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.mvc.view.JspView;
import org.nutz.mvc.view.ServerRedirectView;

@IocBean(create = "init")
@At("/oauth")
public class OauthModule extends BaseModule {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected UserService userService;
	
	@Inject
	protected SysLogService sysLogService;

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

	/**
	 * 统一的OAuth回调入口
	 */
	@At("/?/callback")
	@Ok(">>:/yvr/list/ask")
	public Object callback(String _providerId, HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SocialAuthManager manager = (SocialAuthManager) session.getAttribute("openid_manager");
		if (manager == null)
			throw new SocialAuthException("Not manager found!");
		session.removeAttribute("openid_manager"); //防止重复登录的可能性
		Map<String, String> paramsMap = SocialAuthUtil.getRequestParametersMap(req); 
		AuthProvider provider = manager.connect(paramsMap);
		Profile p = provider.getUserProfile();
		
		OAuthUser oAuthUser = dao.fetchx(OAuthUser.class, p.getProviderId(), p.getValidatedId());
		if (oAuthUser == null) {
			// 如果是github就直接一点,创建同名用户
			if ("github".equals(p.getProviderId())) {
				User user = dao.fetch(User.class, p.getDisplayName());
				if (user == null) {
					user = userService.add(p.getDisplayName(), R.UU32());
					UserProfile profile = dao.fetch(UserProfile.class, user.getId());
					if (profile == null) {
						profile = new UserProfile();
						profile.setUserId(user.getId());
						profile.setNickname(p.getDisplayName());
						profile.setLocation(p.getLocation());
						if (p.getEmail() != null && !"null".equals(p.getEmail())) {
							profile.setEmail(p.getEmail());
							profile.setEmailChecked(true);
						}
						profile.setCreateTime(new Date());
						profile.setUpdateTime(profile.getCreateTime());
						
						dao.insert(profile);
					}
				}
				oAuthUser = new OAuthUser(p.getProviderId(), p.getValidatedId(), user.getId());
				dao.insert(oAuthUser);
				doShiroLogin(session, user, _providerId);
				String returnURL = (String) session.getAttribute("oauth.return.url");
				if (returnURL != null)
					return new ServerRedirectView(returnURL);
				return null;
			}
			
			// TODO 跳到关联引导页
			session.setAttribute("oauth.profile", p);
			return new JspView("jsp.oauth.link");
		}
		User user = dao.fetch(User.class, oAuthUser.getUserId());
		if (user == null) {
			log.debugf("关联用户不存在?!! ==> pid=%s, vid=%s",p.getProviderId(), p.getValidatedId());
			return new HttpStatusView(500);
		}
		doShiroLogin(session, user, _providerId);
		return null;
	}
	
	// 进行Shiro登录
	protected void doShiroLogin(HttpSession session, User user, String _providerId) {
		SecurityUtils.getSubject().login(new OAuthAuthenticationToken(user.getId()));
		session.setAttribute(NutShiro.SessionKey, user.getId());
		sysLogService.async(SysLog.c("method", "用户登陆", null, Toolkit.uid(), "用户通过"+_providerId+" Oauth登陆"));
	}
	
	@At
	public void link() {
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
