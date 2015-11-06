package net.wendal.nutzbook.quartz.job;

import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@IocBean
@Scheduled(cron="0 0 6 * * ?")
public class QiniuUserAvatarUpdateJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO 待完成
	}

}
