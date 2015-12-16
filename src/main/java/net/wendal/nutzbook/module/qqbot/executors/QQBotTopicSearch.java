package net.wendal.nutzbook.module.qqbot.executors;

import net.wendal.nutzbook.bean.qqbot.QQBotMessage;
import net.wendal.nutzbook.bean.qqbot.QQBotRole;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.module.qqbot.QQBotExecutor;
import net.wendal.nutzbook.service.yvr.LuceneSearchResult;
import net.wendal.nutzbook.service.yvr.TopicSearchService;
import net.wendal.nutzbook.util.RedisKey;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * 帖子搜索
 * Created by wendal on 2015/12/17.
 */
@IocBean
public class QQBotTopicSearch implements QQBotExecutor, RedisKey {

    @Inject
    protected TopicSearchService topicSearchService;

    @Inject
    Dao dao;

    @Inject("java:$conf.get('website.urlbase')")
    public String websiteUrlBase = "https://nutz.cn";

    public String execute(QQBotMessage message, QQBotRole role) throws Exception {
        if (message.Message == null)
            return null;
        if (!message.Message.startsWith("帖子搜索 ") || message.Message.length() <= 5) {
            return null;
        }
        String key = message.Message.substring(5).trim();
        List<LuceneSearchResult> topics = topicSearchService.search(key, false);
        if (topics.size() > 5)
            topics = topics.subList(0, 5);
        StringBuilder sb = new StringBuilder("查询结果:");
        if (topics.isEmpty()) {
            sb.append("空");
            return sb.toString();
        }
        for (LuceneSearchResult result : topics) {
            Topic topic = dao.fetch(Topic.class, result.getId());
            if (topic == null)
                continue;
            sb.append(String.format("%s %s/yvr/t/%s\r\n", topic.getTitle(), websiteUrlBase, topic.getId()));
        }
        return sb.toString();
    }
}
