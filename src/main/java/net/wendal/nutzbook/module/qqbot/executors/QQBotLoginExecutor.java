package net.wendal.nutzbook.module.qqbot.executors;

import com.corundumstudio.socketio.SocketIOClient;
import net.wendal.nutzbook.bean.OAuthUser;
import net.wendal.nutzbook.bean.qqbot.QQBotMessage;
import net.wendal.nutzbook.bean.qqbot.QQBotRole;
import net.wendal.nutzbook.crossscreen.CrossScreen;
import net.wendal.nutzbook.module.qqbot.QQBotExecutor;
import net.wendal.nutzbook.service.socketio.SocketioService;
import net.wendal.nutzbook.util.RedisKey;
import net.wendal.nutzbook.util.Toolkit;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.UUID;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

/**
 * 通过QQ登录
 * Created by wendal on 2015/12/17.
 */
@IocBean
public class QQBotLoginExecutor implements QQBotExecutor, RedisKey {

    private static final Log log = Logs.get();

    @Inject
    Dao dao;

    @Inject
    SocketioService socketioService;

    @Inject("java:$conf.get('website.urlbase')")
    public String websiteUrlBase = "https://nutz.cn";

    @Aop("redis")
    public String execute(QQBotMessage message, QQBotRole role) throws Exception {
        if (message.Message == null)
            return null;
        if (message.Message.startsWith("QQ绑定 ") && message.Message.length() > 3) {
            String bindkey = message.Message.substring(3).trim();
            String uid = jedis().get(RKEY_QQBOT_BIND_REQ+bindkey);
            if (Strings.isBlank(uid)) {
                return "未找到绑定请求或已经过期";
            }
            dao.clear(OAuthUser.class, Cnd.where("providerId", "=", "qqlite").and("userId", "=", Integer.parseInt(uid)));
            OAuthUser ouser = new OAuthUser();
            ouser.setProviderId("qqlite");
            ouser.setUserId(Integer.parseInt(uid));
            ouser.setValidatedId(message.Sender);
            dao.insert(ouser);
            return "绑定成功";
        }
        if (message.Message.startsWith("QQ登录 ") && message.Message.length() > 5) {
            OAuthUser ouser = dao.fetch(OAuthUser.class, Cnd.where("providerId", "=", "qqlite").and("validatedId", "=", message.Sender));
            if (ouser == null) {
                return "您没有绑定任何账户";
            }
            String key = message.Message.substring(5).trim();
            UUID c = R.fromUU32(key);
            SocketIOClient client = socketioService.getClient("/qqlite", c);
            if (client == null) {
                log.debug("no such client -->" + key);
                return "没有找到对应的登录会话";
            }

            int uid = ouser.getUserId();
            NutMap map = new NutMap();
            map.put("url", websiteUrlBase+"/yvr/list");
            map.put("t", System.currentTimeMillis());
            map.put("uid", uid);
            String json = Json.toJson(map, JsonFormat.compact());
            log.debug("token json = " + json);
            String token = Toolkit._3DES_encode(CrossScreen.csKEY, json.getBytes());
            client.sendEvent("login_callback", new NutMap().setv("token", token));
            return "登录成功";
        }
        return null;
    }
}
