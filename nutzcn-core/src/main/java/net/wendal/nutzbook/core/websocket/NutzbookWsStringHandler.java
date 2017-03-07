package net.wendal.nutzbook.core.websocket;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.WsHandler;
import org.nutz.plugins.mvc.websocket.WsRoomProvider;

public class NutzbookWsStringHandler implements WsHandler, MessageHandler.Whole<String> {
    
    private static final Log log = Logs.get();
    
    protected Set<String> rooms;
    
    protected WsRoomProvider roomProvider;
    
    protected Session session;
    protected String prefix;
    
    public NutzbookWsStringHandler(String prefix) {
        rooms = new HashSet<>();
        this.prefix = prefix;
    }

    public void onMessage(String message) {
        try {
            NutMap msg = Json.fromJson(NutMap.class, message);
            String action = msg.getString("action");
            if (Strings.isBlank(action))
                return;
            String room = msg.getString("room");
            switch (action) {
            case "join":
                join(room);
                break;
            case "left":
                left(room);
                break;
            default:
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void join(String room) {
        if (!Strings.isBlank(room)) {
            rooms.add(room);
            room = prefix + room;
            log.debugf("session(id=%s) join room(name=%s)", session.getId(), room);
            roomProvider.join(room, session.getId());
        }
    }
    
    public void left(String room) {
        if (!Strings.isBlank(room)) {
            rooms.remove(room);
            room = prefix + room;
            log.debugf("session(id=%s) left room(name=%s)", session.getId(), room);
            roomProvider.left(room, session.getId());
        }
    }

    public void depose() {
        for (String room : rooms) {
            left(room);
        }
    }

    public void setRoomProvider(WsRoomProvider roomProvider) {
        this.roomProvider = roomProvider;
    }
    
    public void setSession(Session session) {
        this.session = session;
    }

}
