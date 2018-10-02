package net.wendal.nutzbook.beepay;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class QQRobotMainSetup implements Setup {

    public void init(NutConfig nc) {
        Daos.createTablesInPackage(nc.getIoc().get(Dao.class), getClass().getPackage().getName() + ".bean", false);
    }

    public void destroy(NutConfig nc) {}

}
