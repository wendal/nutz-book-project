package net.wendal.nutzbook;

import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.nutzbook.core.CoreMainSetup;
import net.wendal.nutzbook.oauth.OauthMainSetup;
import net.wendal.nutzbook.yvr.YvrMainSetup;

public class MainSetup implements Setup {

    @Override
    public void init(NutConfig nc) {
        new CoreMainSetup().init(nc);
        new YvrMainSetup().init(nc);
    }

    @Override
    public void destroy(NutConfig nc) {
        new CoreMainSetup().destroy(nc);
        new YvrMainSetup().destroy(nc);
        new OauthMainSetup().destroy(nc);
    }

}
