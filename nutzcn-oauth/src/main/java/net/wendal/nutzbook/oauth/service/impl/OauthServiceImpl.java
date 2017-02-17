package net.wendal.nutzbook.oauth.service.impl;

import java.util.Properties;

import org.brickred.socialauth.SocialAuthConfig;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import net.wendal.nutzbook.oauth.service.OauthService;

@IocBean(name="oauthService")
public class OauthServiceImpl implements OauthService {

    
    @Inject
    protected PropertiesProxy conf;

    private SocialAuthConfig config;

    public void reload() throws Exception {
        SocialAuthConfig config = new SocialAuthConfig();
        Properties pp = new Properties();
        for (String key : conf.keys()) {
            if (key.startsWith("oauth")) {
                pp.put(key.substring("oauth.".length()), conf.get(key));
            }
        }
        config.load(pp);
        this.config = config;
    }

    @Override
    public SocialAuthConfig getSocialAuthConfig() throws Exception {
        if (config == null)
            reload();
        return config;
    }

}
