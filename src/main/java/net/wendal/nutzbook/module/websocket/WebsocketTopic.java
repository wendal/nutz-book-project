package net.wendal.nutzbook.module.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.wendal.nutzbook.service.yvr.YvrService;
import net.wendal.nutzbook.websocket.NutIocWebSocketConfigurator;

@ServerEndpoint(value = "/yvr/topic/socket", configurator=NutIocWebSocketConfigurator.class)
@IocBean
public class WebsocketTopic extends Endpoint {

    private final static Log log = Logs.get();
    
    protected ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, java.lang.Throwable throwable) {
        sessions.remove(session.getId());
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(new MyMessageHandle(session));
        sessions.put(session.getId(), session);
    }

    @Inject
    protected YvrService yvrService;

    protected class MyMessageHandle implements MessageHandler.Whole<String> {

        private Session session;

        public MyMessageHandle(Session session) {
            this.session = session;
        }

        @Override
        public void onMessage(String message) {
            WebsocketTopic.this.onMessage(message, session);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            NutMap map = Json.fromJson(NutMap.class, message);
            String topicId = map.getString("id");
            int replies = map.getInt("replies");
            Object re = yvrService.check(topicId, replies);
            if (re instanceof Map) {
                session.getAsyncRemote().sendText(Json.toJson(re));
            }
        }
        catch (Throwable e) {
            log.debug("message=" + message, e);
        }
    }
    
    public void trigger(String event, NutMap data) {
        String str = Json.toJson(new NutMap("event", event).setv("data", data), JsonFormat.full());
        sessions.values().forEach((session)->{
            session.getAsyncRemote().sendText(str);
        });
    }
}
