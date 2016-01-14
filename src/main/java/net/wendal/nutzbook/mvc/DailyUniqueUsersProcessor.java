package net.wendal.nutzbook.mvc;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import net.wendal.nutzbook.util.RedisKey;
import net.wendal.nutzbook.util.Toolkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * 统计每天/每小时的独立活跃用户数
 * @author wendal
 *
 */
public class DailyUniqueUsersProcessor extends AbstractProcessor implements RedisKey {

	protected JedisPool pool;
	
	public void process(ActionContext ac) throws Throwable {
		if (pool == null)
			pool = Mvcs.getIoc().get(JedisPool.class);
		int uid = Toolkit.uid();
		if (uid > 0) {
			try (Jedis jedis = pool.getResource()) {
				Pipeline pipe = jedis.pipelined();
				pipe.setbit(RKEY_ONLINE_DAY+Toolkit.today_yyyyMMdd(), uid, true);
				pipe.setbit(RKEY_ONLINE_HOUR+Toolkit.today_yyyyMMddHH(), uid, true);
				pipe.sync();
			}
		}
		doNext(ac);
	}

}
