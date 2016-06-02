package net.wendal.nutzbook.module.yvr;

import java.util.Map;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import net.wendal.nutzbook.service.yvr.YvrService;

//@ServerEndpoint("/yvr/topic/socket")
public class YvrTopicWebSocket {
    
    private static final Log log = Logs.get();
    
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
                session.getBasicRemote().sendText(Json.toJson(re));
            }
        }
        catch (Throwable e) {
            log.debug("message="+message, e);
        }
    }
    
    YvrService yvrService;
}
