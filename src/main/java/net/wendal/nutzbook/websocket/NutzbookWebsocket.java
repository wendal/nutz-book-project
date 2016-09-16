package net.wendal.nutzbook.websocket;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

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

import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

@ServerEndpoint(value = "/websocket", configurator=NutIocWebSocketConfigurator.class)
@IocBean(create="init", depose="depose")
public class NutzbookWebsocket extends Endpoint {
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    protected ConcurrentHashMap<String, NutzbookWsStringHandler> sessions = new ConcurrentHashMap<>();
    
    @Inject
    protected JedisPool jedisPool;
    
    protected JedisPubSub listener;
    
    public static String prefix = "wsroom:";
    
    protected static final Log log = Logs.get();
    
    public void init() {
        listener = new JedisPubSub() {
            public void onPMessage(String patten, String channel, String message) {
                log.debugf("channel=%s, message=%s", channel, message);
                onPublishMessage(channel, message);
            }
        };
        new Thread(){
            public void run() {
                jedisPool.getResource().psubscribe(listener, prefix+"*");
            };
        }.start();
    }
    
    public void depose() {
        if (listener != null)
            listener.punsubscribe(prefix+"*");
    }
    
    @Aop("redis")
    public void onPublishMessage(String channel, String message) {
        for(String id : jedis().hkeys(channel)) {
            NutzbookWsStringHandler handler = sessions.get(id);
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
        NutzbookWsStringHandler handler = sessions.remove(session.getId());
        if (handler != null)
            handler.depose();
    }

    @OnError
    public void onError(Session session, java.lang.Throwable throwable) {
        onClose(session, null);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        NutzbookWsStringHandler handler = new NutzbookWsStringHandler(session, jedisPool);
        session.addMessageHandler(handler);
        sessions.put(session.getId(), handler);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        sessions.get(session.getId()).onMessage(message);
    }
}
