package net.wendal.nutzbook.module;

import java.util.Map;
import java.util.UUID;

import net.wendal.nutzbook.bean.OAuthUser;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.crossscreen.CrossScreen;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.service.socketio.SocketioService;
import net.wendal.nutzbook.util.Toolkit;

import org.nutz.auth.secken.Secken;
import org.nutz.auth.secken.SeckenResp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.corundumstudio.socketio.SocketIOClient;

@At("/secken")
@IocBean
public class SeckenModule extends BaseModule {
	
	private static Log log = Logs.get();

	@Inject SocketioService socketioService;
	
	@Inject Secken secken;
	
	@Inject UserService userService;
	
	@Ok("raw:json")
	@Fail("void")
	@At("/callback/?")
	public String callback(String clientId, @Param("..")Map<String, Object> params) {
		UUID c = R.fromUU32(clientId);
		SeckenResp sr = new SeckenResp(params);
		if (!sr.ok()) {
			log.debug("secken resp not ok -->" + sr);
			return "";
		}
		secken.checkSign(sr);
		SocketIOClient client = socketioService.getClient("/secken", c);
		if (client == null) {
			log.debug("no such client -->" + clientId);
			return "";
		}
		String secken_uid = sr.uid();
		if (secken_uid == null) {
			log.debug("secken resp without uid");
			return "";
		}
		
		OAuthUser ouser = dao.fetchx(OAuthUser.class, "secken", secken_uid);
		if (ouser == null) {
			String username = "secken_" + Lang.sha1(secken_uid).substring(0, 6);
			User user = userService.add(username, secken_uid);
			UserProfile profile = new UserProfile();
			profile.setUserId(user.getId());
			profile.setNickname(username);
			dao.insert(profile);
			ouser = new OAuthUser();
			ouser.setProviderId("secken");
			ouser.setValidatedId(secken_uid);
			ouser.setUserId(user.getId());
			dao.insert(ouser);
		}
		
		int uid = ouser.getUserId();
		NutMap map = new NutMap();
		map.put("url", "https://nutz.cn/yvr/list");
		map.put("t", System.currentTimeMillis());
		map.put("uid", uid);
		String json = Json.toJson(map, JsonFormat.compact());
		log.debug("token json = " + json);
		String token = Toolkit._3DES_encode(CrossScreen.csKEY, json.getBytes());
		
		client.sendEvent("login_callback", new NutMap().setv("token", token));
		return "{\"status\":\"ok\"}";
	}
	
}
