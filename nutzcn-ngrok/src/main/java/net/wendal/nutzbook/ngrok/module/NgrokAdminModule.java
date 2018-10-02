package net.wendal.nutzbook.ngrok.module;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.common.util.RedisKey;
import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.ngrok.NgrokClientHolder;

@IocBean(create="init")
@At("/admin/ngrok")
@Ok("json:full")
public class NgrokAdminModule extends BaseModule {
    
    private static final Log log = Logs.get();
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected NgrokClientHolder ngrokClientHolder;
	
	@RequiresRoles("admin")
	@At("/tokens")
	@Aop("redis")
	public Object getTokens(@Param("..")Pager pager) {
	    // 首先, 根据取出全部uid
	    List<String> uids = new ArrayList<>(jedis().hkeys("ngrok2"));
	    // 自然排序
	    uids.sort((prev, next)-> new Long(prev).compareTo(new Long(next)));
	    pager.setRecordCount(uids.size());
	    int start = pager.getOffset();
	    int end = pager.getOffset() + pager.getPageSize();
        List<NutMap> list = new ArrayList<>();
	    for (int i = start; i < end && i < uids.size(); i++) {
            if (i >= uids.size())
                break;
            String uid = uids.get(i);
            String token = jedis().hget("ngrok2", uid);
            String domain = jedis().hget("ngrok", token);
            NutMap re = new NutMap("uid", uid).setv("token", token).setv("domain", domain);
            User user = dao.fetch(User.class, Long.parseLong(uid));
            if (user != null) {
                re.put("username", user.getName());
                // 查一下最后登录时间
                Double lvtime = jedis().zscore(RedisKey.RKEY_USER_LVTIME, uid);
                if (lvtime == null) {
                    re.put("lvtime", "Never");
                } else {
                    re.put("lvtime", Toolkit.createAt(new Date(lvtime.longValue())));
                }
            }
            list.add(re);
        }
	    return ajaxOk(new QueryResult(list, pager));
	}
	
	@POST
    @RequiresRoles("admin")
    @At("/token/delete")
    @Aop("redis")
    public Object deleteToken(@Param("token")String token) {
        // 首先, 删除ngrok2上的token
        jedis().hdel("ngrok", token);
        for(Entry<String, String> en : jedis().hgetAll("ngrok2").entrySet()) {
            if (en.getValue().equals(token))
                jedis().hdel("ngrok2", en.getKey());
        }
        return ajaxOk("");
    }
	
	@POST
    @RequiresRoles("admin")
    @At("/token/clear")
    @Aop("redis")
    public Object clearToken(@Param("day")int day) {
        // 首先, 删除ngrok2上的token
	    if (day < 30)
	        day = 30;
        for(Entry<String, String> en : jedis().hgetAll("ngrok2").entrySet()) {
            String uid = en.getKey();
            String token = en.getValue();
            User user = dao.fetch(User.class, Long.parseLong(uid));
            if (user == null || user.isLocked()) {
                jedis().hdel("ngrok2", uid);
                jedis().hdel("ngrok", token);
            } else {
                Double lvtime = jedis().zscore(RedisKey.RKEY_USER_LVTIME, uid);
                if (lvtime == null) {
                    jedis().hdel("ngrok2", uid);
                    jedis().hdel("ngrok", token);
                } else {
                    long diff = System.currentTimeMillis() - lvtime.longValue();
                    log.debug("最后登录时间: " + Toolkit.createAt(new Date(lvtime.longValue())));
                    if (diff > day * 86400L*1000) {
                        jedis().hdel("ngrok2", uid);
                        jedis().hdel("ngrok", token);
                    }
                }
            }
        }
        return ajaxOk("");
    }
    
    @RequiresRoles("admin")
    @At("/client/status")
    public Object clientStatus(HttpServletRequest req) {
        NutMap map = new NutMap();
        map.put("status", ngrokClientHolder.getClient().status);
        map.put("error", ngrokClientHolder.getClient().error);
        map.put("id", ngrokClientHolder.getClient().id);
        map.put("reqid_map", ngrokClientHolder.getClient().reqIdMap);
        return ajaxOk(map);
    }
    
    @POST
    @RequiresRoles("admin")
    @At("/client/start")
    public Object clientStart() {
        log.debug("ngrok client start ...");
        if (ngrokClientHolder.getClient().status != 1)
            ngrokClientHolder.getClient().start();
        return ajaxOk("");
    }

    @POST
    @RequiresRoles("admin")
    @At("/client/stop")
    public Object clientStop() {
        log.debug("ngrok client stop ...");
        if (ngrokClientHolder.getClient().status == 1)
            ngrokClientHolder.getClient().stop();
        return ajaxOk("");
    }
}
