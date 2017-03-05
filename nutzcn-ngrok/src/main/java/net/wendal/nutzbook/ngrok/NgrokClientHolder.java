package net.wendal.nutzbook.ngrok;

import java.util.Map;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.ngrok.client.NgrokClient;

import net.wendal.nutzbook.common.util.OnConfigureChange;

@IocBean(create="init")
public class NgrokClientHolder implements OnConfigureChange {

    @Inject
    protected PropertiesProxy conf;
    
    protected NgrokClient client;
    
    public NgrokClient getClient() {
        return client;
    }

    public void init() {
        client = NgrokClient.make(conf, "ngrok.client.");
        if (conf.getBoolean("ngrok.client.auto_start", false) && !Strings.isBlank(conf.get("ngrok.client.auth_token"))) {
            client.start();
        }
    }

    public void configureChanged(Map<String, Object> props) {
        if (!props.containsKey("ngrok.auto_start"))
            return;
        if (client.status == 1) {
            client.stop();
        }
        init();
    }
}
