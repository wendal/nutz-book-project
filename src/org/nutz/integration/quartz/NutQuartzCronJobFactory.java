package org.nutz.integration.quartz;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;

@IocBean(create="init")
public class NutQuartzCronJobFactory {
	
	private static final Log log = Logs.get();

	@Inject protected PropertiesProxy conf;
	
	@Inject protected Scheduler scheduler;
	
	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		String prefix = "cron.";
		for (String key : conf.getKeys()) {
			if (key.length() < prefix.length()+1 || !key.startsWith(prefix))
				continue;
			String name = key.substring(prefix.length());
			String cron = conf.get(key);
			log.debugf("job define name=%s cron=%s", name, cron);
			Class<?> klass = null;
			if (name.contains(".")) {
				klass = Class.forName(name);
			} else {
				klass = Class.forName(getClass().getPackage().getName() + ".job." + name);
			}
			JobDetail job = JobBuilder.newJob((Class<? extends Job>) klass).build();
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name)
				    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
				    .build();
			scheduler.scheduleJob(job, trigger);
		}
	}

}
