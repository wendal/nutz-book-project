package net.wendal.nutzbook.shiro.cache;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.JedisPubSub;

public class CachePubSub extends JedisPubSub {

    private static final Log log = Logs.get();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onPMessage(String pattern, String channel, String message) {
        if (message.startsWith(LCacheManager.me.id))
            return;
        log.debugf("channel=%s, msg=%s", channel, message);
        String cacheName = channel.substring(LCacheManager.PREFIX.length());
        LCache cache = LCacheManager.me.caches.get(cacheName);
        if (cache != null)
            cache._remove(message.substring(LCacheManager.me.id.length() + 1));
    }
}
