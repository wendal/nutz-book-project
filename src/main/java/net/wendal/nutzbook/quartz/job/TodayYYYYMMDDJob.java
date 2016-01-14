package net.wendal.nutzbook.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.wendal.nutzbook.util.Toolkit;

public class TodayYYYYMMDDJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		Toolkit.updateTodayString();
	}

}
