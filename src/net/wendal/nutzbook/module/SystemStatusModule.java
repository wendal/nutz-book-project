package net.wendal.nutzbook.module;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.statistics.StatisticsGateway;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/sys/stat")
public class SystemStatusModule extends BaseModule {
	
//	private static final Log log = Logs.get();

	@Inject CacheManager cacheManager;
	
	@At
	@Ok("json:full")
	public Object cache() {
		List<NutMap> list = new ArrayList<NutMap>();
		for (String cacheName : cacheManager.getCacheNames()) {
			Cache cache = cacheManager.getCache(cacheName);
			if (cache == null)
				continue;
			StatisticsGateway sg = cache.getStatistics();
			if (sg == null)
				continue;
			NutMap re = new NutMap();
			re.put("name", cacheName);
			for (Method method : sg.getClass().getMethods()) {
				if (method.getParameterTypes().length != 0)
					continue;
				if (method.getName().equals("toString") || method.getName().equals("hashCode") || method.getName().equals("getClass"))
					continue;
				try {
					Object z = method.invoke(sg);
					if (z == null)
						continue;
					if (z instanceof ExtendedStatistics.Result) {
						NutMap p = new NutMap();
						for (Method m2 : sg.getClass().getMethods()) {
							if (m2.getParameterTypes().length != 0)
								continue;
							if (m2.getName().equals("toString") || m2.getName().equals("hashCode") || m2.getName().equals("getClass"))
								continue;
							p.put(m2.getName(), method.invoke(z));
						}
						z = p;
					}
					if (String.valueOf(z).equals("NaN")) // fix in nutz 1.b.53
						continue;
					re.put(method.getName(), z);
				} catch (Exception e) {
				}
			}
			list.add(re);
		}
		return ajaxOk(list);
	}
}
