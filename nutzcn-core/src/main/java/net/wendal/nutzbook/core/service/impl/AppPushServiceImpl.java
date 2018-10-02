package net.wendal.nutzbook.core.service.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.nutz.aop.interceptor.async.Async;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import net.wendal.nutzbook.common.util.OnConfigureChange;
import net.wendal.nutzbook.core.service.AppPushService;

/**
 * 推送服务,当前用jpush实现
 * 
 * @author wendal
 *
 */
@IocBean(create = "init", name="appPushService")
public class AppPushServiceImpl implements AppPushService, OnConfigureChange {

	private static final Log log = Logs.get();

	protected JPushClient jpush;

	protected Sender xmpush;

	@Inject
	protected PropertiesProxy conf;

	public void alert(long userId, String alert, String description, Map<String, String> extras) {
	    if (conf.getBoolean("jpush.enable", false))
            alertJpush(userId, alert, extras);
        if (conf.getBoolean("xmpush.enable", false))
            alertXmPush(userId, alert, description, extras);
	}

	public void message(long userId, String message, Map<String, String> extras) {
		if (conf.getBoolean("jpush.enable", false))
			messageJpush(userId, message, extras);
		if (conf.getBoolean("xmpush.enable", false))
			messageXmPush(userId, message, extras);
	}

	private void alertJpush(long userId, String alert, Map<String, String> extras) {
		AndroidNotification android = AndroidNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		IosNotification ios = IosNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		Notification notif = Notification.newBuilder().addPlatformNotification(android).addPlatformNotification(ios).build();
		cn.jpush.api.push.model.PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all());
		builder.setAudience(Audience.alias("u_" + userId));
		builder.setNotification(notif);
		Options options = Options.newBuilder().setApnsProduction(true).build();
		builder.setOptions(options);
		sendJPush(builder.build());
	}

	private void alertXmPush(long userId, String alert, String description, Map<String, String> extras) {
		Message.Builder builder = new Message.Builder().title(alert).description(description);
		if (conf.has("xmpush.appPkg")) {
            builder.restrictedPackageName(conf.get("xmpush.appPkg"));
        }
		for (Entry<String, String> en : extras.entrySet()) {
			builder.extra(en.getKey(), en.getValue());
		}
		sendMxPush(builder.build(), "u_" + userId);
	}

	private void messageXmPush(long userId, String message, Map<String, String> extras) {
		Message.Builder builder = new Message.Builder().title(message).description(message).passThrough(1).payload(message);
		if (conf.has("xmpush.appPkg")) {
		    builder.restrictedPackageName(conf.get("xmpush.appPkg"));
		}
		for (Entry<String, String> en : extras.entrySet()) {
			builder.extra(en.getKey(), en.getValue());
		}
		sendMxPush(builder.build(), "u_" + userId);
	}

	private void messageJpush(long userId, String message, Map<String, String> extras) {
		AndroidNotification android = AndroidNotification.newBuilder().setAlert("").setTitle(message).addExtras(extras).build();
		IosNotification ios = IosNotification.newBuilder().setAlert("").addExtras(extras).build();
		Notification notif = Notification.newBuilder().addPlatformNotification(android).addPlatformNotification(ios).build();
		cn.jpush.api.push.model.PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all());
		builder.setAudience(Audience.alias("u_" + userId));
		builder.setNotification(notif);
		Options options = Options.newBuilder().setApnsProduction(true).build();
		builder.setOptions(options);
		sendJPush(builder.build());
	}

	@Async
	public void doJpush(String name, JPushClient jpush, PushPayload payload) {
		if (conf.getBoolean("jpush.enable", false))
			try {
				PushResult re = jpush.sendPush(payload);
				log.debugf("%s result=%s", name, re);
			} catch (Exception e) {
				log.debugf("send %s fail", name);
			}
	}

	private void sendJPush(PushPayload payload) {
		doJpush("jpush", jpush, payload);
	}

	@Async
	public void doMxPush(Message message, String alias) {
		if (conf.getBoolean("xmpush.enable", false))
			sendMxPush(message, alias);
	}

	private void sendMxPush(Message message, String alias) {
		if (xmpush == null)
			return;
		try {
			Result re = xmpush.sendToAlias(message, alias, 3);
			log.info("xmpush result=" + re);
		} catch (Exception e) {
			log.debugf("send to %s fail", alias, e);
		}
	}

	public void init() {
		reload();
	}
	
	public void reload() {
	    try {
            if (!conf.getBoolean("jpush.enable", false)) {
                log.info("jpush disabled");
                jpush = null;
            } else {
                jpush = new JPushClient(conf.get("jpush.masterSecret"), conf.get("jpush.appKey"));
            }
            if (!conf.getBoolean("xmpush.enable", false)) {
                log.info("xmpush disabled");
                xmpush = null;
            } else {
                if ("sandbox".equalsIgnoreCase("xmpush.mode")) {
                    log.warn("xmpush using sandbox mode!");
                    Constants.useSandbox();
                } else {
                    Constants.useOfficial();
                }
                xmpush = new Sender(conf.get("xmpush.appSecret"));
            }
        }
        catch (Exception e) {
            log.debug("bad jpush key?", e);
        }
	}

    public void configureChanged(Map<String, Object> props) {
        if (props.keySet().contains("jpush.enable") || props.keySet().contains("xmpush.enable"))
            this.reload();
    }
}
