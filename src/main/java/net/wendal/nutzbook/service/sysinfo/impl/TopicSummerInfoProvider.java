package net.wendal.nutzbook.service.sysinfo.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;

@IocBean
public class TopicSummerInfoProvider extends AbstractSysInfoProvider {

    public String name() {
        return "帖子统计";
    }

    public String description() {
        return "统计最近1天,一周,一个月的发帖情况";
    }

    public List<NutMap> fetch() {
        List<NutMap> re = new ArrayList<>();
        // 统计今天的发帖量
        NutMap map = new NutMap();
        map.put("name", "最近24小时新贴");
        map.put("value", dao.count(Topic.class, Cnd.where("createTime", ">", from24h())));
        re.add(map);
        map = new NutMap();
        map.put("name", "最近24小时回帖");
        map.put("value", dao.count(TopicReply.class, Cnd.where("createTime", ">", from24h())));
        re.add(map);

        map = new NutMap();
        map.put("name", "最近一周新贴");
        map.put("value", dao.count(Topic.class, Cnd.where("createTime", ">", from7d())));
        re.add(map);
        map = new NutMap();
        map.put("name", "最近一周回帖");
        map.put("value", dao.count(TopicReply.class, Cnd.where("createTime", ">", from7d())));
        re.add(map);

        map = new NutMap();
        map.put("name", "最近30天新贴");
        map.put("value", dao.count(Topic.class, Cnd.where("createTime", ">", from30d())));
        re.add(map);
        map = new NutMap();
        map.put("name", "最近30天回帖");
        map.put("value", dao.count(TopicReply.class, Cnd.where("createTime", ">", from30d())));
        re.add(map);
        

        map = new NutMap();
        map.put("name", "总帖子");
        map.put("value", dao.count(Topic.class, null));
        re.add(map);
        map = new NutMap();
        map.put("name", "总回帖");
        map.put("value", dao.count(TopicReply.class, null));
        re.add(map);
        
        return re;
    }
}
