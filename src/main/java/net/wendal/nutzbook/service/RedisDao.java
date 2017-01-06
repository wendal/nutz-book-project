package net.wendal.nutzbook.service;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import net.wendal.nutzbook.service.yvr.YvrService;

/**
 * 尝试做个redis的dao
 * @author wendal
 *
 */
@IocBean
public class RedisDao {
	
	@Inject
	protected YvrService yvrService;
	
	@Inject
	protected Dao dao;

	@Aop("redis")
	public <T> List<T> queryByZset(Class<T> klass, String zkey, Pager pager) {
		long now = System.currentTimeMillis();
		Long count = jedis().zcount(zkey, 0, now);
		List<T> list = new ArrayList<T>();
		if (count != null && count.intValue() != 0) {
			pager.setRecordCount(count.intValue());
			Set<String> ids = jedis().zrevrangeByScore(zkey, now, 0, pager.getOffset(), pager.getPageSize());
			for (String id : ids) {
				T t = dao.fetch(klass, id);
				if (t == null)
					continue;
				list.add(t);
			}
		}
		return list;
	}

    @Aop("redis")
    public String znext(String zkey, String id) {
        Long thisRank = jedis().zrank(zkey, id);
        if (thisRank == null)
            return null;
        Set<String> re = jedis().zrange(zkey, thisRank+1, thisRank+2);
        if (re.isEmpty())
            return null;
        return re.iterator().next();
    }
    
    @Aop("redis")
    public String zprev(String zkey, String id) {
        Long thisRank = jedis().zrank(zkey, id);
        if (thisRank == null)
            return null;
        Set<String> re = jedis().zrange(zkey, thisRank-1, thisRank);
        if (re.isEmpty())
            return null;
        return re.iterator().next();
    }
}
