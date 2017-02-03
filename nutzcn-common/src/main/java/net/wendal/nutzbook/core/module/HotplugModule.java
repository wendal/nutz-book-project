package net.wendal.nutzbook.core.module;

import java.io.File;
import java.util.Map;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.plugins.hotplug.HotPlug;

@IocBean
@At("/hot")
public class HotplugModule {
    
    @Inject
    protected HotPlug hotPlug;

    //@RequiresAuthentication
    @POST
    @At
    @AdaptBy(type=UploadAdaptor.class)
    public void add(@Param("..")Map<String, Object> params) throws Exception {
        hotPlug.add(new File("D:\\hotplug-cms.jar"));
    }
}
