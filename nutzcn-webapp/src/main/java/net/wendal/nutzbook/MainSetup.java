package net.wendal.nutzbook;

import java.util.ArrayList;
import java.util.List;

import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.nutzbook.core.CoreMainSetup;

public class MainSetup implements Setup {
    
    protected CoreMainSetup core;
    protected List<Setup> setups;

    @Override
    public void init(NutConfig nc) {
        core = new CoreMainSetup();
        core.init(nc);
        setups = new ArrayList<>();
        for (String name : nc.getIoc().getNamesByType(Setup.class)) {
            Setup setup = nc.getIoc().get(Setup.class, name);
            setups.add(setup);
            setup.init(nc);
        }
    }

    @Override
    public void destroy(NutConfig nc) {
        core.destroy(nc);
        for (Setup setup : setups) {
            setup.destroy(nc);
        }
    }

}
