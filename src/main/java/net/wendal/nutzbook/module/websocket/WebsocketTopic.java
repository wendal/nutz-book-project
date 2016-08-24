package net.wendal.nutzbook.module;

import java.util.Map;

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

import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import net.wendal.nutzbook.service.yvr.YvrService;

@ServerEndpoint(value = "/yvr/topic/socket")
public class WebsocketTopic extends Endpoint {

    private final static Log log = Logs.get();

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.error("onClose");
    }

    @OnError
    public void onError(Session session, java.lang.Throwable throwable) {
        log.error("onError");
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(new MyMessageHandle(session));
    }

    private YvrService yvrService;

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
        if (yvrService == null)
            yvrService = Mvcs.ctx().getDefaultIoc().get(YvrService.class);
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
}
