package net.wendal.nutzbook.cxfdemo;

import javax.servlet.ServletRegistration.Dynamic;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class CxfdemoMainSetup implements Setup {

    public static Ioc ioc;

    @Override
    public void init(NutConfig nc) {
        ioc = nc.getIoc();
        // 因为是hotplug插件,那我们就主动注册一下
        Dynamic dyn = nc.getServletContext().addServlet("cxf", CxfServlet.class);
        if (dyn != null) {
            dyn.addMapping("/ws/*");
        }
    }

    @Override
    public void destroy(NutConfig nc) {
        // TODO 怎么卸载呢?
    }

}
