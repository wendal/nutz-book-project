package net.wendal.nutzbook.ngrok.module;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.plugins.apidoc.annotation.Api;

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.module.BaseModule;

@Api(name="Ngrok管理", description="ngrok内网穿透服务相关的api")
@IocBean
@At("/ngrok")
@Ok("json:full")
public class NgrokModule extends BaseModule {
	
	@Inject
	protected PropertiesProxy conf;

	@RequiresUser
	@At
	@Ok("->:/yvr/links/ngrok")
	public Object me() {
	    return null;
//	    if (conf.getBoolean("ngrok.server.enable", false)) {
//	        return null;
//	    }
//		return new ViewWrapper(new RawView(null), "本服务处于关闭状态");
	}

	@RequiresUser
	@At("/config/download")
	@Ok("raw:xml")
	public Object getConfigureFile(HttpServletResponse resp) throws UnsupportedEncodingException {
	    long userId = Toolkit.uid();
		String token = getAuthToken(userId);
//		if (token == null || conf.getBoolean("ngrok.server.enable", false)) {
//			return HTTP_403;
//		}
		String filename = URLEncoder.encode("ngrok.yml", Encoding.UTF8);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		String[] lines = new String[]{
				"server_addr: "+conf.get("ngrok.server.srv_host", "wendal.cn") + ":" + conf.getInt("ngrok.server.srv_port", 4443),
				"trust_host_root_certs: true",
				"auth_token: " + token,
				""
		};
		return Strings.join("\r\n", lines);
	}
	
	@Aop("redis")
	protected String getAuthToken(long userId) {
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
