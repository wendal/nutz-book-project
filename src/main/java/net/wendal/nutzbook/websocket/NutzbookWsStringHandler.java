package net.wendal.nutzbook.websocket;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class NutzbookWsStringHandler implements MessageHandler.Whole<String> {
    
    protected JedisPool jedisPool;
    
    protected Session session;
    
    protected NutMap attrs;
    
    protected Set<String> rooms;
    
    private static final Log log = Logs.get();
    
    public NutzbookWsStringHandler(Session session, JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.session = session;
        attrs = new NutMap();
        rooms = new HashSet<>();
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
            log.debugf("session(id=%s) join room(name=%s)", session.getId(), room);
            try (Jedis jedis = jedisPool.getResource()) {
                rooms.add(room);
                jedis.hset(NutzbookWebsocket.prefix+room, session.getId(), "");
            }
        }
    }
    
    public void left(String room) {
        if (!Strings.isBlank(room)) {
            log.debugf("session(id=%s) left room(name=%s)", session.getId(), room);
            try (Jedis jedis = jedisPool.getResource()) {
                rooms.add(room);
                jedis.hdel(NutzbookWebsocket.prefix+room, session.getId());
            }
        }
    }

    public Session getSession() {
        return session;
    }
    
    public NutzbookWsStringHandler setAttr(String key, Object val) {
        this.attrs.put(key, val);
        return this;
    }
    
    public Object getAttr(String key) {
        return this.attrs.get(key);
    }
    
    public String getId() {
        return session.getId();
    }

    public void depose() {
        for (String room : rooms) {
            left(room);
        }
    }
}
