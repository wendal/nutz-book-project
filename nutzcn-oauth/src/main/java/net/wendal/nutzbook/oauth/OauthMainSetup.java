package net.wendal.nutzbook.oauth;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class OauthMainSetup implements Setup {

    @Override
    public void init(NutConfig nc) {
        Daos.createTablesInPackage(nc.getIoc().get(Dao.class), getClass().getPackage().getName() + ".bean", false);
    }

    @Override
    public void destroy(NutConfig nc) {
        // org.brickred.socialauth.util.HttpUtil 把一个内部类注册到SSLContext,擦!
        try {
            SSLContext.getDefault().init(null, null, new SecureRandom());
        }
        catch (Exception e) {
        }
    }
    
}
