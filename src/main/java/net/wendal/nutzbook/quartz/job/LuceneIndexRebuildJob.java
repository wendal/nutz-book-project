package net.wendal.nutzbook.quartz.job;

import java.io.IOException;

import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.wendal.nutzbook.service.yvr.TopicSearchService;

@IocBean
@Scheduled(cron="0 0 3 * * ?")
public class LuceneIndexRebuildJob implements Job {

    @Inject
    protected TopicSearchService topicSearchService;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            topicSearchService.rebuild();
        }
        catch (IOException e) {
            throw new JobExecutionException(e);
        }
    }
}
