package net.wendal.nutzbook.service.socketio;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;

@IocBean
public class SimpleChatService {

	
    // SocketIOClient, AckRequest and Data could be ommited
    @OnEvent("add user")
    public void onAddUser(SocketIOClient client, Object data, AckRequest ackRequest) {
        client.sendEvent("login", new NutMap().setv("numUsers", 1));
        client.joinRoom("nutzbook");
        client.set("username", data);
        client.set("room", "nutzbook");
    }
    
    @OnEvent("user joined")
    public void onUserJoined(SocketIOClient client, Object data, AckRequest ackRequest) {
    	NutMap re = new NutMap().setv("username", client.get("username"));
    	client.getNamespace().getRoomOperations(client.get("room")).sendEvent("user joined", re);
    }

    @OnEvent("new message")
    public void onNewMessage(SocketIOClient client, Object data, AckRequest ackRequest) {
    	NutMap re = new NutMap().setv("username", client.get("username")).setv("message", data);
    	client.getNamespace().getRoomOperations(client.get("room")).sendEvent("new message", re);
    }
    
    @OnEvent("typing")
    public void onTyping(SocketIOClient client, Object data, AckRequest ackRequest) {
    	NutMap re = new NutMap().setv("username", client.get("username"));
    	client.getNamespace().getRoomOperations(client.get("room")).sendEvent("typing", re);
    }
    
    @OnEvent("stop typing")
    public void onStopTyping(SocketIOClient client, Object data, AckRequest ackRequest) {
    	NutMap re = new NutMap().setv("username", client.get("username"));
    	client.getNamespace().getRoomOperations(client.get("room")).sendEvent("stop typing", re);
    }
}
