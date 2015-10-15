package net.wendal.nutzbook.service.socketio;

import net.wendal.nutzbook.bean.yvr.TopicWatch;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.zbus.MsgBus;
import org.nutz.plugins.zbus.MsgEventHandler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnEvent;

@IocBean
public class TopicWatcheService implements MsgEventHandler<TopicWatch> {
	
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
	public Object call(MsgBus bus, TopicWatch event) throws Exception {
		if (namespace != null) {
			BroadcastOperations opt = namespace.getRoomOperations("topic:"+event.getId());
			if (opt != null) {
				opt.sendEvent("reload", "");
			}
		}
		return null;
	}
}
