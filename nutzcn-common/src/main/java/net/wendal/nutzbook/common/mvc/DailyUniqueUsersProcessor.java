package net.wendal.nutzbook.common.mvc;

import org.nutz.integration.jedis.JedisAgent;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import net.wendal.nutzbook.common.util.RedisKey;
import net.wendal.nutzbook.common.util.Toolkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 统计每天/每小时的独立活跃用户数
 * @author wendal
 *
 */
public class DailyUniqueUsersProcessor extends AbstractProcessor implements RedisKey {

	protected JedisAgent jedisAgent;
	
	private static final Log log = Logs.get();
	
	public void process(ActionContext ac) throws Throwable {
		try {
            if (jedisAgent == null)
                jedisAgent = ac.getIoc().get(JedisAgent.class);
            long uid = Toolkit.uid();
            if (uid > 0) {
            	try (Jedis jedis = jedisAgent.getResource()) {
            	    jedis.setbit(RKEY_ONLINE_DAY+Toolkit.today_yyyyMMdd(), uid, true);
            		//pipe.setbit(RKEY_ONLINE_HOUR+Toolkit.today_yyyyMMddHH(), uid, true);
            	    jedis.zadd(RKEY_USER_LVTIME, System.currentTimeMillis(), ""+uid);
            	}
            }
        }
        catch (Exception e) {
            if (e instanceof JedisConnectionException) {
                log.debug("jedis is down? ignore error");
            } else {
                log.debug("something wrong? ignore error", e);
            }
        }
		doNext(ac);
	}

}
