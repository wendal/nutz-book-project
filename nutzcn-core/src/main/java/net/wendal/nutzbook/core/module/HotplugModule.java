package net.wendal.nutzbook.core.module;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

@IocBean
@At("/admin/hotplug")
@Ok("json")
public class HotplugModule extends BaseModule {
    
    @At
    public Object list(@Param("active")boolean activeOnly) throws Exception {
        List<NutResource> _list = Scans.me().scan("hotplug/", ".+.(js|json)$");
        List<NutMap> list = new ArrayList<>();
        for (NutResource nr : _list) {
            list.add(Json.fromJson(NutMap.class, nr.getReader()));
        }
        return ajaxOk(new QueryResult(list, new Pager()));
    }
    
}
