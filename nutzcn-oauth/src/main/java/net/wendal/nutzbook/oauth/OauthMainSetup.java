package net.wendal.nutzbook.oauth;

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
    public void destroy(NutConfig nc) {}
    
}
