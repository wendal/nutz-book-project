package net.wendal.nutzbook.service.socketio;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

@IocBean
public class SimpleChatService {

	
    // SocketIOClient, AckRequest and Data could be ommited
    @OnEvent("add user")
    public void onAddUser(SocketIOClient client, Object data, AckRequest ackRequest) {
    	String name = data == null ? R.UU32(client.getSessionId()) : data.toString();
    	if (Strings.isBlank(name)) {
    		name = R.UU32(client.getSessionId());
    	}
        client.sendEvent("login", new NutMap().setv("numUsers", client.getNamespace().getAllClients().size()));
        client.joinRoom("nutzbook");
        client.set("username", data);
        client.set("room", "nutzbook");
        

    	NutMap re = new NutMap().setv("username", client.get("username")).setv("numUsers", client.getNamespace().getAllClients().size());
    	client.getNamespace().getRoomOperations(client.get("room")).sendEvent("user joined", re);
    }
    
    @OnEvent("new message")
    public void onNewMessage(SocketIOClient client, Object data, AckRequest ackRequest) {
    	if (data == null)
    		return;
    	String val = data.toString().trim();
    	if (Strings.isBlank(val))
    		return;
    	if (val.startsWith("#")) {
    		if (val.length() == 1)
    			return;
    		val = val.substring(1);
    		String[] tmp = val.split(" ", 2);
    		if (tmp.length != 2) {
    			return;
    		}
    		String uid = tmp[0].trim();
    		String msg = tmp[1].trim();
    		if (uid.isEmpty() || msg.isEmpty())
    			return;
    		for (SocketIOClient c2 : client.getNamespace().getAllClients()) {
				if (uid.equals(c2.get("username"))) {
					NutMap re = new NutMap().setv("username", client.get("username")).setv("message", "私聊:"+msg);
					c2.sendEvent("new message", re);
					return;
				}
			}
    	}
    	
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
    
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
    	if (client == null)
    		return;
    	SocketIONamespace nm = client.getNamespace();
    	if (nm == null)
    		return;
    	String room = client.get("room");
    	if (room == null)
    		return;
    	BroadcastOperations bo = nm.getRoomOperations(room);
    	if (bo == null)
    		return;
    	NutMap re = new NutMap().setv("username", client.get("username")).setv("numUsers", nm.getAllClients().size());
    	bo.sendEvent("user left", re);
    }
}
