package net.wendal.nutzbook.module.yvr;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.module.BaseModule;
import net.wendal.nutzbook.util.Markdowns;

import org.nutz.dao.Cnd;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Each;
import org.nutz.lang.Files;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import com.redfin.sitemapgenerator.WebSitemapUrl.Options;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

/**
 * 负责输出rss和sitemap的模块
 * @author wendal
 *
 */
@IocBean(create="init")
@At("/yvr")
@Fail("http:500")
public class YvrSeoModule extends BaseModule {
	
	@Inject("refer:conf")
	PropertiesProxy conf;

	@At
	@Ok("raw:xml")
	public String rss() throws IOException, FeedException {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		String urlbase = conf.get("topic_seo.urlbase", "https://nutz.cn");
		feed.setLink(urlbase);
		feed.setTitle(conf.get("topic_seo.title", "Nutz社区"));
		feed.setDescription(conf.get("topic_seo.description", "一个有爱的社区"));
		
		feed.setAuthor(conf.get("topic_seo.author", "wendal"));
		feed.setEncoding("UTF-8");
		feed.setLanguage("zh-cn");
		
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;
		List<Topic> list = dao.query(Topic.class, Cnd.orderBy().desc("createTime"), dao.createPager(1, 10));
		for (Topic topic: list) {
			dao.fetchLinks(topic, "author");
			dao.fetchLinks(topic.getAuthor(), null);
			entry = new SyndEntryImpl();
            entry.setTitle(topic.getTitle());
            entry.setLink(urlbase + "/yvr/t/" + topic.getId());
            entry.setPublishedDate(topic.getCreateTime());
            description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(Markdowns.toHtml(topic.getContent(), urlbase));
            entry.setDescription(description);
            entry.setAuthor(topic.getAuthor().getLoginname());
            entries.add(entry);
		}
		
		feed.setEntries(entries);
		if (list.size() > 0) {
			feed.setPublishedDate(list.get(0).getCreateTime());
		}

        SyndFeedOutput output = new SyndFeedOutput();
        return output.outputString(feed, true);
	}
	
	@At
	@Ok("raw:xml")
	public File sitemap() throws MalformedURLException, ParseException{
		String tmpdir = conf.get("topic_seo.tmp_dir", "/tmp");
		Files.createDirIfNoExists(tmpdir);
		final WebSitemapGenerator gen = new WebSitemapGenerator(urlbase, new File(tmpdir));
		gen.addUrl(urlbase + "/yvr/list");
		dao.each(Topic.class, Cnd.orderBy().desc("createTime"), dao.createPager(1, 1000), new Each<Topic>() {
			public void invoke(int index, Topic topic, int length)  {
				try {
					Options options = new Options(urlbase + "/yvr/t/" + topic.getId());
					// TODO 从redis读取最后更新时间
					//options.lastMod(topic.getCreateAt());
					WebSitemapUrl url = new WebSitemapUrl(options);
					gen.addUrl(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		List<File> list = gen.write();
		if (list.size() > 0)
			return list.get(0);
		return null;
	}
}
