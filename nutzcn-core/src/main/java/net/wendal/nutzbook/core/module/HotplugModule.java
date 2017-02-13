package net.wendal.nutzbook.core.module;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.plugins.hotplug.HotPlug;
import org.nutz.plugins.hotplug.HotPlugConfig;

@IocBean
@At("/admin/hotplug")
@Ok("json")
public class HotplugModule extends BaseModule {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected HotPlug hotPlug;
    
    @RequiresRoles("admin")
    @At
    public Object list(@Param("active")boolean activeOnly) throws Exception {
        List<HotPlugConfig> list = new ArrayList<>(HotPlug.getActiveHotPlug().values());
        for (HotPlugConfig hc : list) {
            log.debugf("hc name=%s version=%s enable=%s", hc.getName(), hc.getVersion(), hc.isEnable());
        }
        if (!activeOnly) {
            for (HotPlugConfig hc_file : HotPlug.getHotPlugJarList(true)) {
                boolean flag = true;
                for (HotPlugConfig hc : HotPlug.getActiveHotPlug().values()) {
                    if (hc_file.getSha1().equals(hc.getSha1())) {
                        flag = false;
                        break;
                    }
                }
                if (flag)
                    list.add(hc_file);
            }
        }
        return ajaxOk(new QueryResult(list, new Pager()));
    }

    @SuppressWarnings("deprecation")
    @RequiresRoles("admin")
    @POST
    @At
    @AdaptBy(type=UploadAdaptor.class)
    public NutMap add(@Param("file")TempFile f) throws Exception {
        boolean ok = hotPlug.add(f.getFile());
        return ajaxOk(ok);
    }
    
    @RequiresRoles("admin")
    @POST
    @At
    public NutMap disable(@Param("name")String name) throws Exception {
        hotPlug.disable(name);
        return ajaxOk(null);
    }
    
    @RequiresRoles("admin")
    @POST
    @At
    public NutMap enable(@Param("sha1")String sha1) throws Exception {
        for (HotPlugConfig hc : HotPlug.getHotPlugJarList(true)) {
            if (sha1.equals(hc.getSha1())) {
                hotPlug.enable(new File(hc.getOriginPath()), null);
                return ajaxOk(null);
            }
        }
        return ajaxFail("没找到该插件");
    }
    
}
