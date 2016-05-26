package net.wendal.nutzbook.module;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.nutz.log.Log;
import org.nutz.log.Logs;

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

	private class MyMessageHandle implements MessageHandler.Whole<String> {
		private RemoteEndpoint.Basic remote;

		public MyMessageHandle(RemoteEndpoint.Basic remote) {
			this.remote = remote;
		}

		@Override
		public void onMessage(String s) {
			try {
				remote.sendText(s);
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
}
