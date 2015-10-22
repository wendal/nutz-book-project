package net.wendal.nutzbook.service;

import java.io.IOException;
import java.util.Map;

import org.nutz.integration.zbus.ZBusProducer;
import org.nutz.integration.zbus.annotation.ZBusConsumer;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;

import com.google.gson.JsonSyntaxException;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
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
@ZBusConsumer(mq="jpush")
@IocBean
public class PushService implements MessageHandler {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected JPushClient jpush;
	
	@Inject("java:$conf.getBoolean('jpush.enable', true)")
	protected boolean enable;
	
	@Inject("java:$zbus.getProducer('jpush')")
	protected ZBusProducer jPushProducer;
	
	public void alert(int userId, String alert, Map<String, String> extras) {
		AndroidNotification android = AndroidNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		IosNotification ios = IosNotification.newBuilder().setAlert(alert).addExtras(extras).build();
		Notification notif = Notification.newBuilder().addPlatformNotification(android).addPlatformNotification(ios).build();
		cn.jpush.api.push.model.PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all());
		builder.setAudience(Audience.alias("u_"+ userId));
		builder.setNotification(notif);
		jPushProducer.async(builder.build().toString()); // 发送到总线,等待对应的服务处理
	}
	
	public void message(int userId, String message, Map<String, String> extras) {
		AndroidNotification android = AndroidNotification.newBuilder().setAlert("").setTitle(message).addExtras(extras).build();
		IosNotification ios = IosNotification.newBuilder().setAlert("").addExtras(extras).build();
		Notification notif = Notification.newBuilder().addPlatformNotification(android).addPlatformNotification(ios).build();
		cn.jpush.api.push.model.PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all());
		builder.setAudience(Audience.alias("u_"+ userId));
		builder.setNotification(notif);
		jPushProducer.async(builder.build().toString()); // 发送到总线,等待对应的服务处理
	}
	
	@Override
	public void handle(Message msg, Session sess) throws IOException {
		if (!enable)
			return;
		String body = msg.getBodyString();
		log.debug("body = " + body);
		try {
			PushResult re = jpush.sendPush(body);
			log.debugf("jpush result=%s", re);
		} catch (APIConnectionException | APIRequestException | JsonSyntaxException | IllegalArgumentException e) {
			log.debug("send jpush fail", e);
		}
	}

}
