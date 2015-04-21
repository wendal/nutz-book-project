var ioc = {
		scheduler : {
			type : "org.quartz.Scheduler",
			factory: "org.quartz.impl.StdSchedulerFactory#getDefaultScheduler",
			events : {
				create : "start",
				depose : "shutdown",
			},
			fields : {
				jobFactory : {refer:"nutQuartzJobFactory"}
			}
		}
};