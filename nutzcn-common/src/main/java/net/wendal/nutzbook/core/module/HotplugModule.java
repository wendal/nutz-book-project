package net.wendal.nutzbook.core.module;

import java.util.ArrayList;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
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
    
    @Inject
    protected HotPlug hotPlug;
    
    @RequiresRoles("admin")
    @At
    public Object list() throws Exception {
        return ajaxOk(new QueryResult(new ArrayList<>(HotPlug.plugins.values()), new Pager()));
    }

    @SuppressWarnings("deprecation")
    @RequiresRoles("admin")
    @POST
    @At
    @AdaptBy(type=UploadAdaptor.class)
    public HotPlugConfig add(@Param("file")TempFile f) throws Exception {
        return hotPlug.add(f.getFile());
    }
    
    @RequiresRoles("admin")
    @POST
    @At
    public void remove(@Param("name")String name) throws Exception {
        hotPlug.remove(name);
    }
}
