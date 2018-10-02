package net.wendal.nutzbook.core.quartz.job;

import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.wendal.nutzbook.common.util.Toolkit;

@Scheduled(cron="0 0 * * * ?")
@IocBean
public class TodayYYYYMMDDJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		Toolkit.updateTodayString();
	}

}
