package net.wendal.nutzbook.quartz.job;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@IocBean
public class EhcacheFlushJob implements Job {

	@Inject
	protected CacheManager cacheManager;
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		for (String name : cacheManager.getCacheNames()) {
            Cache cache = this.cacheManager.getCache(name);
            if (cache != null) {
            	if (cache.getCacheConfiguration().isClearOnFlush())
            		cache.getCacheConfiguration().setClearOnFlush(false);
                cache.flush();
            }
        }
	}

}
