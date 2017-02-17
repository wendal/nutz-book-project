package net.wendal.nutzbook.oauth.module;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.core.service.ConfigureService;
import net.wendal.nutzbook.oauth.service.OauthService;

@IocBean(create = "init")
@At("/admin/oauth")
public class OAuthConfigureModule extends BaseModule {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected ConfigureService configureService;
	
    @Inject
    protected OauthService oauthService;

    @RequiresPermissions("configure:set")
	@Ok("json")
	@At("/save")
	public Object save(@Param("..")Map<String, Object> props) throws Exception {
        try {
            for (Entry<String, Object> en : props.entrySet()) {
                String key = en.getKey();
                if (key.startsWith("oauth.")) {
                    String value = en.getValue().toString();
                    if ("0".equals(value))
                        value = "";
                    configureService.update(key, value, false);
                    continue;
                }
            }
            configureService.doReload();
            oauthService.reload();
        }
        catch (Exception e) {
            log.warn("save oauth configure fail", e);
            return ajaxFail(e.getMessage());
        }
        return ajaxOk(null);
	}

}
