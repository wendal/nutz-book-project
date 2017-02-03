package net.wendal.nutzbook.oauth;

import org.nutz.dao.Dao;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.nutzbook.oauth.bean.OAuthUser;

public class OauthMainSetup implements Setup {

    @Override
    public void init(NutConfig nc) {
        nc.getIoc().get(Dao.class).create(OAuthUser.class, false);
    }

    @Override
    public void destroy(NutConfig nc) {}
    
}
