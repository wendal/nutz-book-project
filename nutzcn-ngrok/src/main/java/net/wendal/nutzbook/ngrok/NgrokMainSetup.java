package net.wendal.nutzbook.ngrok;

import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class NgrokMainSetup implements Setup {

    @Override
    public void init(NutConfig nc) {
        nc.getIoc().get(NgrokClientHolder.class);
    }

    @Override
    public void destroy(NutConfig nc) {
        nc.getIoc().get(NgrokClientHolder.class).getClient().stop();
    }

}
