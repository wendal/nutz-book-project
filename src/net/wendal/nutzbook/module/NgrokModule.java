package net.wendal.nutzbook.module;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.RawView;
import org.nutz.mvc.view.ViewWrapper;

import net.wendal.nutzbook.bean.OAuthUser;
import net.wendal.nutzbook.bean.User;

@IocBean
@At("/ngrok")
public class NgrokModule extends BaseModule {

	@RequiresUser
	@At
	@Ok("->:/yvr/links/ngrok")
	public Object me(@Attr(scope = Scope.SESSION, value = "me") int userId) {
		String token = getAuthToken(userId);
		if (token == null) {
			return new ViewWrapper(new RawView(null), "抱歉,当前仅允许github登陆的用户使用");
		}
		return null;
	}

	@RequiresUser
	@At("/config/download")
	@Ok("raw:xml")
	public Object getConfigureFile(@Attr(scope = Scope.SESSION, value = "me") int userId, HttpServletResponse resp) throws UnsupportedEncodingException {
		String token = getAuthToken(userId);
		if (token == null) {
			return HTTP_403;
		}
		String filename = URLEncoder.encode("ngrok.yml", Encoding.UTF8);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		String[] lines = new String[]{
				"server_addr: nutz.cn:4443",
				"trust_host_root_certs: true",
				"auth_token: " + token,
				""
		};
		return Strings.join("\r\n", lines);
	}
	
	@Aop("redis")
	public String getAuthToken(int userId) {
		int count = dao.count(OAuthUser.class, Cnd.where("providerId", "=", "github").and("userId", "=", userId));
		if (count != 1 && userId > 2) {
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
