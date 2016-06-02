package net.wendal.nutzbook.module;

import java.io.IOException;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.wendal.nutzbook.service.yvr.YvrService;

import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

@ServerEndpoint(value = "/yvr/topic/socket")
public class WebsocketTopic extends Endpoint {

	private final static Log log = Logs.get();

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		log.error("onClose");
	}

	@Override
	public void onError(Session session, java.lang.Throwable throwable) {
		log.error("onError");
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		RemoteEndpoint.Basic remote = session.getBasicRemote();
		session.addMessageHandler(new MyMessageHandle(remote));
	}

	private YvrService yvrService;

	private class MyMessageHandle implements MessageHandler.Whole<String> {
		private RemoteEndpoint.Basic remote;

		public MyMessageHandle(RemoteEndpoint.Basic remote) {
			this.remote = remote;
		}

		@Override
		public void onMessage(String message) {
			if (yvrService == null)
				yvrService = Mvcs.ctx().getDefaultIoc().get(YvrService.class);
			try {
				NutMap map = Json.fromJson(NutMap.class, message);
				String topicId = map.getString("id");
				int replies = map.getInt("replies");
				Object re = yvrService.check(topicId, replies);
				if (re instanceof Map) {
					remote.sendText(Json.toJson(re));
				}
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
}
