package net.wendal.nutzbook.module;

import java.io.IOException;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;

import net.wendal.nutzbook.plugin.IPlugin;
import net.wendal.nutzbook.plugin.PluginMaster;

@IocBean
@Ok("json")
@At("/plugins")
public class PluginModule extends BaseModule {
    private static final Log log = Logs.get();

    @Inject
    protected PluginMaster pluginMaster;
    
    @At("/")
    @Ok("jsp:jsp.plugins.index")
    public void index() {}
    
    @RequiresRoles("admin")
    @POST
    @At
    @AdaptBy(type=WhaleAdaptor.class)
    public NutMap add(@Param("key")String key, 
                    @Param("className")String className, 
                    @Param("file")TempFile tmp, @Param("args")String[] args) 
                            throws IOException {
        try {
            byte[] buf = Streams.readBytesAndClose(tmp.getInputStream());
            IPlugin plugin;
            if (tmp.getSubmittedFileName().endsWith(".class"))
                plugin = pluginMaster.build(className, buf);
            else if (tmp.getSubmittedFileName().endsWith(".jar") || tmp.getSubmittedFileName().endsWith(".zip"))
                plugin = pluginMaster.buildFromJar(className, buf);
            else
                throw new RuntimeException("only accept class or jar!!");
            pluginMaster.register(key, plugin, args);
            return new NutMap("ok", true);
        }
        catch (Exception e) {
            log.debug("plugin load error", e);
            return new NutMap("ok", false).setv("msg", e.getMessage());
        }
    }
    
    @RequiresRoles("admin")
    @At
    public Object list() {
        return pluginMaster.getPlugins().keySet();
    }
    
    @RequiresRoles("admin")
    @At
    @POST
    public void delete(String key) {
        pluginMaster.remove(key);
    }
}
