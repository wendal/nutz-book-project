package net.wendal.nutzbook.module;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.RawView;
import org.nutz.mvc.view.ViewWrapper;

import org.nutz.plugins.apidoc.annotation.Api;
import net.wendal.nutzbook.bean.OAuthUser;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.util.Toolkit;

@Api(name="Ngrok管理", description="ngrok内网穿透服务相关的api")
@IocBean
@At("/ngrok")
public class NgrokModule extends BaseModule {
	
	@Inject("java:$conf.get('ngrok.server')")
	protected String ngrokServer;
	
	@Inject("java:$conf.getBoolean('ngrok.github_only', true)")
	protected boolean githubOnly;

	@RequiresUser
	@At
	@Ok("->:/yvr/links/ngrok")
	public Object me() {
		int userId = Toolkit.uid();
		String token = getAuthToken(userId);
		if (token == null) {
			return new ViewWrapper(new RawView(null), "抱歉,当前仅允许github登陆的用户使用");
		}
		return null;
	}

	@RequiresUser
	@At("/config/download")
	@Ok("raw:xml")
	public Object getConfigureFile(HttpServletResponse resp) throws UnsupportedEncodingException {
		int userId = Toolkit.uid();
		String token = getAuthToken(userId);
		if (token == null) {
			return HTTP_403;
		}
		String filename = URLEncoder.encode("ngrok.yml", Encoding.UTF8);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		String[] lines = new String[]{
				"server_addr: "+ngrokServer,
				"trust_host_root_certs: true",
				"auth_token: " + token,
				""
		};
		return Strings.join("\r\n", lines);
	}
	
	@Aop("redis")
	public String getAuthToken(int userId) {
		int count = dao.count(OAuthUser.class, Cnd.where("providerId", "=", "github").and("userId", "=", userId));
		if (count != 1 && userId > 2 && githubOnly) {
			return null;
		}
		User user = dao.fetch(User.class, userId);
		String token = jedis().hget("ngrok2", ""+userId);
		
		if (token == null) {
			token = R.UU32();
			jedis().hset("ngrok2", ""+userId, token);
			jedis().hset("ngrok", token, user.getName() + ".ngrok");
		}
		return token;
	}
}
