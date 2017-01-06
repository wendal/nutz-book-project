package net.wendal.nutzbook.service.pubsub;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.JedisPool;

@IocBean(depose="depose")
public class PubSubService {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected JedisPool jedisPool;
    
    List<PubSubProxy> list = new ArrayList<>();

    public void reg(String patten, PubSub pb) {
        PubSubProxy proxy = new PubSubProxy(patten, pb);
        list.add(proxy);
        new Thread(()->jedisPool.getResource().psubscribe(proxy, patten)).start();
    }
    
    @Aop("redis")
    public void fire(String channel, String message) {
        log.debugf("publish channel=%s msg=%s", channel, message);
        jedis().publish(channel, message);
    }

    public void depose() {
        for (PubSubProxy proxy : list)
            proxy.punsubscribe(proxy.patten);
    }
}
