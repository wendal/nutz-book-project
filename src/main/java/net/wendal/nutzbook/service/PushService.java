package net.wendal.nutzbook.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.aop.interceptor.async.Async;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 * 推送服务,当前用jpush实现
 * @author wendal
 *
 */
@IocBean(create="init")
public class PushService {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected JPushClient jpush;
	
	protected Map<String, JPushClient> jpushs;
	
	@Inject
	protected PropertiesProxy conf;
	
	public void alert(int userId, String alert, Map<String, String> extras) {
		AndroidNotification android = AndroidNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		IosNotification ios = IosNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		Notification notif = Notification.newBuilder().addPlatformNotification(android).addPlatformNotification(ios).build();
		cn.jpush.api.push.model.PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all());
		builder.setAudience(Audience.alias("u_"+ userId));
		builder.setNotification(notif);
		Options options = Options.newBuilder().setApnsProduction(true).build();
		builder.setOptions(options);
		send(builder.build());
	}
	
	public void message(int userId, String message, Map<String, String> extras) {
		AndroidNotification android = AndroidNotification.newBuilder().setAlert("").setTitle(message).addExtras(extras).build();
		IosNotification ios = IosNotification.newBuilder().setAlert("").addExtras(extras).build();
		Notification notif = Notification.newBuilder().addPlatformNotification(android).addPlatformNotification(ios).build();
		cn.jpush.api.push.model.PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all());
		builder.setAudience(Audience.alias("u_"+ userId));
		builder.setNotification(notif);
		Options options = Options.newBuilder().setApnsProduction(true).build();
		builder.setOptions(options);
		send(builder.build());
	}
	
	@Async
	public void send(PushPayload payload) {
		try {
			PushResult re = jpush.sendPush(payload);
			log.debugf("jpush result=%s", re);
		} catch (Exception e) {
			log.debug("send jpush fail", e);
		}
		
		// 第三方客户端的推送账户
		for (Entry<String, JPushClient> en : jpushs.entrySet()) {
			try {
				PushResult re = en.getValue().sendPush(payload);
				log.debugf("%s result=%s", en.getKey(), re);
			} catch (Exception e) {
				log.debugf("send %s fail", en.getKey(), e);
			}
		}
	}
	
	public void init() {
		jpushs = new LinkedHashMap<>();
		// 支持10个够了吧
		for (int i = 1; i < 11; i++) {
			String prefix = "jpush"+i;
			boolean enable = conf.getBoolean(prefix+".enable", false);
			if (!enable)
				continue;
			String masterSecret = conf.get(prefix+".masterSecret");
			String appKey = conf.get(prefix+".appKey");
			if (Strings.isBlank(masterSecret) || Strings.isBlank(appKey)) {
				log.warn(prefix+".enable=true, but masterSecret/appKey is NULL or emtry");
				continue;
			}
			JPushClient jpush = new JPushClient(masterSecret, appKey);
			jpushs.put(prefix, jpush);
		}
	}
}
