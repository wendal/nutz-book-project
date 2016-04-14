package net.wendal.nutzbook.quartz.job;

import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.wendal.nutzbook.service.syslog.SysLogService;

@Scheduled(cron="0 59 23 * * ?")
@IocBean
public class SysLogTableCreateJob implements Job {
	
	@Inject
	protected SysLogService sysLogService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		sysLogService.checkTable();
	}

}
