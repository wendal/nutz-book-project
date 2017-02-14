package net.wendal.nutzbook;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.plugins.hotplug.Hotplug;

public class MainSetup implements Setup {

    public void init(NutConfig nc) {
        Ioc ioc = nc.getIoc();
        ioc.get(Hotplug.class).setupInit();
    }

    public void destroy(NutConfig nc) {
        Ioc ioc = nc.getIoc();
        ioc.get(Hotplug.class).setupDestroy();
    }

}
