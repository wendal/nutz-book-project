package net.wendal.nutzbook.service;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.gexin.rp.sdk.http.IGtPush;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 推送服务,当前用jpush实现
 * @author wendal
 *
 */
@IocBean(create="init")
public class PushService {

	/**
	 * 通知类型: 帖子被回复
	 */
	public static int PUSH_TYPE_REPLY = 0;
	/**
	 * 通知类型: 被at
	 */
	public static int PUSH_TYPE_AT = 1;

	private static final Log log = Logs.get();
	
	@Inject
	protected JPushClient jpush;
	
	protected Map<String, JPushClient> jpushs;
	
	protected Map<String, IGtPush> gtpushs;
	
	@Inject
	protected PropertiesProxy conf;
	
	public void alert(int userId, String alert, Map<String, String> extras) {
		alertJpush(userId, alert, extras);
		alertGtPush(userId, alert, extras);
	}
	
	public void message(int userId, String message, Map<String, String> extras) {
		messageJpush(userId, message, extras);
	}
	
	public void alertJpush(int userId, String alert, Map<String, String> extras) {
		AndroidNotification android = AndroidNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		IosNotification ios = IosNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		Notification notif = Notification.newBuilder().addPlatformNotification(android).addPlatformNotification(ios).build();
		cn.jpush.api.push.model.PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all());
		builder.setAudience(Audience.alias("u_"+ userId));
		builder.setNotification(notif);
		Options options = Options.newBuilder().setApnsProduction(true).build();
		builder.setOptions(options);
		sendJPush(builder.build());
	}
	
	public void alertGtPush(int userId, String alert, Map<String, String> extras) {
		
	}
	
	public void messageJpush(int userId, String message, Map<String, String> extras) {
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
		try {
			PushResult re = jpush.sendPush(payload);
			log.debugf("%s result=%s", name, re);
		} catch (Exception e) {
			log.debugf("send %s fail", name, e);
		}
	}
	
	public void sendJPush(PushPayload payload) {
		doJpush("jpush", jpush, payload);
		// 第三方客户端的推送账户
		for (Entry<String, JPushClient> en : jpushs.entrySet()) {
			doJpush(en.getKey(), en.getValue(), payload);
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
		
		gtpushs = new LinkedHashMap<>();
		for (int i = 1; i < 11; i++) {
			String prefix = "gtpush"+i;
			boolean enable = conf.getBoolean(prefix+".enable", false);
			if (!enable)
				continue;
			String masterSecret = conf.get(prefix+".masterSecret");
			String appKey = conf.get(prefix+".appKey");
			if (Strings.isBlank(masterSecret) || Strings.isBlank(appKey)) {
				log.warn(prefix+".enable=true, but masterSecret/appKey is NULL or emtry");
				continue;
			}
			IGtPush gtpush = new IGtPush(appKey, masterSecret);
			gtpushs.put(prefix, gtpush);
		}
	}
}
