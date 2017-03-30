package net.wendal.nutzbook.dwmaster;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.nutzbook.core.service.AuthorityService;

public class DwmasterMainSetup implements Setup {

    @Override
    public void init(NutConfig nc) {
        Daos.createTablesInPackage(nc.getIoc().get(Dao.class), getClass().getPackage().getName() + ".bean", false);
        nc.getIoc().get(AuthorityService.class).initFormPackage(getClass().getPackage().getName());
    }

    @Override
    public void destroy(NutConfig nc) {}

}
