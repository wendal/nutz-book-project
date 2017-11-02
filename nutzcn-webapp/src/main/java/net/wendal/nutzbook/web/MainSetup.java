package net.wendal.nutzbook.web;

import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.plugins.hotplug.Hotplug;

import net.wendal.nutzbook.core.websocket.NutzbookWebsocket;

public class MainSetup implements Setup {

    @Override
    public void init(NutConfig nc) {
        ServerContainer sc = (ServerContainer)nc.getServletContext().getAttribute(ServerContainer.class.getName());
        if (sc != null) {
            try {
                sc.addEndpoint(NutzbookWebsocket.class);
            } catch (DeploymentException e) {

                e.printStackTrace();
            }
        }
        Ioc ioc = nc.getIoc();
        ioc.get(Hotplug.class).setupInit();
    }

    @Override
    public void destroy(NutConfig nc) {
        Ioc ioc = nc.getIoc();
        ioc.get(Hotplug.class).setupDestroy();
    }

}
