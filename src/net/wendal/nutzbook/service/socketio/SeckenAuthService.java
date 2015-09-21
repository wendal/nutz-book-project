package net.wendal.nutzbook.service.socketio;

import org.nutz.auth.secken.Secken;
import org.nutz.auth.secken.SeckenResp;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnEvent;

@IocBean
public class SeckenAuthService {

	private static Log log = Logs.get();
	
	@Inject Dao dao;
	
	@Inject Secken secken;
	
	@OnEvent("get_auth_qr")
	public void getAuthQr(SocketIOClient client, Object data, AckRequest ackRequest) {
		NutMap re = new NutMap();
		try {
			// TODO 可配置
			SeckenResp resp = secken.getAuth(1, "https://nutz.cn/secken/callback?c="+R.UU32(client.getSessionId())).check();
			String url = resp.qrcode_url();
			re.put("ok", true);
			re.put("url", url);
		} catch (Exception e) {
			log.debug("获取洋葱授权二维码识别", e);
			re.put("msg", "获取洋葱授权二维码识别");
		}
		client.sendEvent("new_auth_qr", re);
	}
	
	@OnConnect
	public void welcome(SocketIOClient client) {
		client.sendEvent("login", "");
	}
}
