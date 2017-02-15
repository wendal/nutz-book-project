package net.wendal.nutzbook.dwmaster;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class DwmasterMainSetup implements Setup {

    @Override
    public void init(NutConfig nc) {
        Daos.createTablesInPackage(nc.getIoc().get(Dao.class), getClass().getPackage().getName() + ".bean", false);
    }

    @Override
    public void destroy(NutConfig nc) {}

}
