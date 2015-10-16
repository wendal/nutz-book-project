package net.wendal.nutzbook.service;

import java.io.IOException;

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
