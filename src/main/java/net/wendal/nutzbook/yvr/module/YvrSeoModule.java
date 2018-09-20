package net.wendal.nutzbook.yvr.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.nutz.dao.Cnd;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Each;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

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

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.yvr.bean.Topic;
import net.wendal.nutzbook.yvr.util.Markdowns;

/**
 * 负责输出rss和sitemap的模块
 * @author wendal
 *
 */
@Api(name="SEO模块", description="负责输出rss和sitemap等SEO相关的文件", match=ApiMatchMode.NONE)
@IocBean(create="init")
@At("/yvr")
@Fail("http:500")
public class YvrSeoModule extends BaseModule {
	
	@Inject("refer:conf")
	PropertiesProxy conf;

	/**
	 * 全文输出
	 */
	@At
	@Ok("raw:xml")
	public String rss() throws IOException, FeedException {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		String urlbase = websiteUrlBase;
		feed.setLink(urlbase);
		feed.setTitle(conf.get("website.title", "Nutz社区"));
		feed.setDescription(conf.get("website.description", "一个有爱的社区"));
		
		feed.setAuthor(conf.get("website.author", "wendal"));
		feed.setEncoding("UTF-8");
		feed.setLanguage("zh-cn");
		
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;
		List<Topic> list = dao.query(Topic.class, Cnd.orderBy().desc("createTime"), dao.createPager(1, 10));
		for (Topic topic: list) {
			dao.fetchLinks(topic, "author");
			entry = new SyndEntryImpl();
            entry.setTitle(StringEscapeUtils.unescapeHtml(topic.getTitle()));
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
	
	/**
	 * 输出Sitemap
	 */
	@At
	@Ok("raw:xml")
	public File sitemap() throws MalformedURLException, ParseException{
		String tmpdir = conf.get("website.tmp_dir", "/tmp");
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
	
	/**
	 * 根据Markdown生成文档
	 */
	@At({"/links/?"})
	@Ok("beetl:/yvr/website/links.html")
	public Object page(String type) throws IOException {
		String path = "/doc/" + type + ".md";
		InputStream ins = getClass().getClassLoader().getResourceAsStream(path);
		if (ins == null)
			return HTTP_404;
		String cnt = Streams.readAndClose(new InputStreamReader(ins, Encoding.CHARSET_UTF8));
		String[] tmp = cnt.split("\n", 2);
		String title = tmp[0].trim().split(" ", 2)[1].trim();
		return _map("title", title, "cnt", cnt, "current_user", fetch_userprofile(Toolkit.uid()));
	}
}
