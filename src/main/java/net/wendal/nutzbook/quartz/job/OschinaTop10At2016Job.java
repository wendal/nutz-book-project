package net.wendal.nutzbook.quartz.job;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nutz.dao.Dao;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.wendal.nutzbook.bean.justfuck.OschinaTop10;

@Scheduled(cron="0 */1 * * * ?")
@IocBean
public class OschinaTop10At2016Job implements Job {

    @Inject Dao dao;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        NutMap map = fuckit();
        OschinaTop10 top10 = new OschinaTop10();
        top10.setCnt(Json.toJson(map, JsonFormat.compact()));
        top10.setCt(System.currentTimeMillis());
        dao.insert(top10);
    }
    
    public static NutMap fuckit() {
        NutMap map = new NutMap();
        try {
            Request req = Request.create("http://www.oschina.net/project/top_cn_2016?sort=1", METHOD.GET);
            req.getHeader().set("User-Agent", "FUCK");
            req.getHeader().remove("Accept-Encoding");
            Response resp = Sender.create(req).send();
            String cnt = Streams.readAndClose(resp.getReader());
            Document doc = Jsoup.parse(cnt);
            Elements eles = doc.select(".list > dl");
            for (Element element : eles) {
                //System.out.println(element);
                String name = element.select("dt > a").attr("href");
                name = name.substring(name.lastIndexOf('/')+1);
                //System.out.println("name == " + name);
                
                String count = element.select(".num > strong").text();
                //System.out.println(count);
                //System.out.println("===================");
                map.put(name, Integer.parseInt(count.trim()));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(Json.toJson(map));
        return map;
    }
    
    public static void main(String[] args) {
        fuckit();
    }
}
