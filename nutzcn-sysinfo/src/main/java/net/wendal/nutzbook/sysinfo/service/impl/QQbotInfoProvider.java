package net.wendal.nutzbook.sysinfo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

@IocBean
public class QQbotInfoProvider extends AbstractSysInfoProvider {

    public String name() {
        return "QQ机器人转发服务";
    }

    public String description() {
        return "经由本服务器转发的QQ机器人消息服务";
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<NutMap> fetch() {
        List<NutMap> re = new ArrayList<>();
        NutMap map;
        
        map = new NutMap();
        map.put("name", "总转发配置数");
        map.put("value", Lang.filter((Map)conf, "qqbot.route.", null, null, null).size());
        re.add(map);
        
        // TODO 统计转发消息数
        
        return re;
    }

}
