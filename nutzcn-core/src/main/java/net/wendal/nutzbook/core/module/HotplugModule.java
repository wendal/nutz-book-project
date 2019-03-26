package net.wendal.nutzbook.core.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
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
    
    @RequiresAuthentication
    @At
    public Object list(@Param("active")boolean activeOnly) throws Exception {
        List<NutResource> _list = Scans.me().scan("hotplug/", ".+.(js|json)$");
        List<NutMap> list = new ArrayList<>();
        for (NutResource nr : _list) {
            list.add(Json.fromJson(NutMap.class, nr.getReader()));
        }
        Subject su = SecurityUtils.getSubject();
        // 限制一下, 仅管理员能看到其他菜单,直至想到其他办法
        if (!SecurityUtils.getSubject().hasRole("admin")) {
            Iterator<NutMap> it = list.iterator();
            while (it.hasNext()) {
                NutMap cur = it.next();
                List<NutMap> menus = cur.getAsList("menu", NutMap.class);
                if (menus == null)
                    continue;
                Iterator<NutMap> it2 = menus.iterator();
                while (it2.hasNext()) {
                    NutMap menu = it2.next();
                    if (menu.containsKey("role") && !su.hasRole(menu.getString("role"))) {
                        it2.remove();
                    }
                }
            }
        }
        return ajaxOk(new QueryResult(list, new Pager()));
    }
    
}
