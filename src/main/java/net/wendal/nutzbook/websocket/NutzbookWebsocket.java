package net.wendal.nutzbook.websocket;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@ServerEndpoint(value = "/websocket", configurator=NutIocWebSocketConfigurator.class)
@IocBean(create="init", depose="depose")
public class NutzbookWebsocket extends Endpoint implements PubSub {
    
    // WebSocketSession只对当前JVM是唯一的
    /** UU32 --> WebSocketSession*/
    protected ConcurrentHashMap<String, NutzbookWsStringHandler> _sessions = new ConcurrentHashMap<>();
    /** WebSocketSession.id --> UU32 */
    protected ConcurrentHashMap<String, String> sessionIds = new ConcurrentHashMap<String, String>();
    
    @Inject
    protected PubSubService pubSubService;
    
    @Inject
    protected JedisAgent jedisAgent;
    
    public static String prefix = "wsroom:";
    
    protected static final Log log = Logs.get();
    
    public void init() {
        pubSubService.reg(prefix+"*", this);
    }
    
    @Aop("redis")
    public void onMessage(String channel, String message) {
        for(String uu32 : jedis().hkeys(channel)) {
            log.debug("uu32 == " + uu32);
            NutzbookWsStringHandler handler = _sessions.get(uu32);
            if (handler == null)
                continue;
            Session session = handler.getSession();
            if (!session.isOpen()) {
                continue;
            }
            session.getAsyncRemote().sendText(message);
        };
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        String uu32 = sessionIds.remove(session.getId());
        if (uu32 == null)
            return;
        NutzbookWsStringHandler handler = _sessions.remove(uu32);
        if (handler != null)
            handler.depose();
    }

    @OnError
    public void onError(Session session, java.lang.Throwable throwable) {
        onClose(session, null);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        String uu32 = R.UU32();
        NutzbookWsStringHandler handler = new NutzbookWsStringHandler(uu32, session, jedisAgent);
        session.addMessageHandler(handler);
        sessionIds.put(session.getId(), uu32);
        _sessions.put(uu32, handler);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        NutzbookWsStringHandler handler = getHandler(session.getId());
        if (handler != null)
            handler.onMessage(message);
    }
    
    protected NutzbookWsStringHandler getHandler(String sessionId) {
        String uu32 = sessionIds.get(sessionId);
        if (uu32 == null)
            return null;
        return _sessions.get(uu32);
    }
    
    public void depose() {
        
    }
}
