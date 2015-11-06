package net.wendal.nutzbook.service.socketio;

import java.io.IOException;

import org.nutz.integration.zbus.annotation.ZBusConsumer;
import org.nutz.ioc.loader.annotation.IocBean;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnEvent;

@ZBusConsumer(mq="topic-watch")
@IocBean
public class TopicWatcheService implements MessageHandler {
	
	protected SocketIONamespace namespace;
	
	@OnEvent("watch")
	public void getAuthQr(SocketIOClient client, Object data, AckRequest ackRequest) {
		client.joinRoom("topic:"+data);
		client.sendEvent("ping", "");
		if (namespace == null)
			namespace = client.getNamespace();
	}

	@OnConnect
	public void welcome(SocketIOClient client) {
		client.sendEvent("login", "");
	}

	@Override
	public void handle(Message msg, Session sess) throws IOException {
		if (namespace != null) {
			BroadcastOperations opt = namespace.getRoomOperations("topic:"+msg.getBodyString());
			if (opt != null) {
				opt.sendEvent("reload", "");
			}
		}
	}
}
