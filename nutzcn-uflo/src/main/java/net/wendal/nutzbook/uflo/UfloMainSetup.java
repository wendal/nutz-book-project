package net.wendal.nutzbook.uflo;

import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class UfloMainSetup implements Setup {
    
    public void init(NutConfig nc) {
        nc.getIoc().get(null, "uflo.taskService");
    }

    public void destroy(NutConfig nc) {
    }

}
