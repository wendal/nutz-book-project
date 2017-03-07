package net.wendal.nutzbook.core.websocket;

import java.util.Set;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.WsHandler;

@ServerEndpoint(value = "/websocket", configurator=NutIocWebSocketConfigurator.class)
@IocBean(create="init", depose="depose")
public class NutzbookWebsocket extends AbstractWsEndpoint implements PubSub {

    protected static final Log log = Logs.get();
    
    @Inject
    protected PubSubService pubSubService;
    
    public static String prefix = "wsroom:";
    
    public void init() {
        pubSubService.reg(prefix+"*", this);
    }
    
    public WsHandler createHandler(Session session, EndpointConfig config) {
        return new NutzbookWsStringHandler(prefix);
    }
    
    @Aop("redis")
    public void onMessage(String channel, String message) {
        log.debugf("GET PubSub channel=%s msg=%s", channel, message);
        Set<String> wsids = roomProvider.wsids(channel);
        log.debugf("room=%s size=%s", channel, wsids.size());
        for(String wsid : wsids) {
            Session session = _sessions.get(wsid);
            if (session == null)
                continue;
            if (!session.isOpen()) {
                _sessions.remove(wsid);
                continue;
            }
            session.getAsyncRemote().sendText(message);
        };
    }
    
    public void depose(){}
}
