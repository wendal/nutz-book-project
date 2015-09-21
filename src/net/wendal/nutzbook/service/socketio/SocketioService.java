package net.wendal.nutzbook.service.socketio;

import java.io.Closeable;
import java.util.UUID;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;

@IocBean(create="init", depose="close")
public class SocketioService implements Closeable {

	protected static final Log log = Logs.get();
	
	protected SocketIOServer srv;
	
	@Inject("java:$conf.get('socketio.port')")
	protected int port;

	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
    @OnConnect
    public void onConnectHandler(SocketIOClient client) {
        log.debug("client ... " + client.getSessionId());
    }

    @OnDisconnect
    public void onDisconnectHandler(SocketIOClient client) {
    	log.debug("client ... " + client.getSessionId());
    }
	
	// 服务器初始化代码
	
	public void init() {
		if (port < 1)
			port = 81;
		log.debug("socketio start at port="+port);
		
		Configuration config = new Configuration();
	    //config.setHostname("localhost");
	    config.setPort(port);
	    // TODO 实现一个Nutz版本的JsonSupport
//	    config.setJsonSupport(jsonSupport);
	    
	    srv = new SocketIOServer(config);
	    
	    // 简单聊天
	    srv.addNamespace("/chat").addListeners(ioc.get(SimpleChatService.class));
	    
	    // 洋葱验证
	    srv.addNamespace("/secken").addListeners(ioc.get(SeckenAuthService.class));
	    
	    srv.start();
	}
	
	public void close() {
		log.info("shutting down socketio");
		if (srv != null)
			srv.stop();
		log.info("shutting down socketio done");
		Lang.quiteSleep(1000); // 需要等1秒, netty才能清理完全
	}

	public SocketIOClient getClient(UUID clientId) {
		return srv.getClient(clientId);
	}
}
