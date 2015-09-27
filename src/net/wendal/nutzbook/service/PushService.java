package net.wendal.nutzbook.service;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.zbus.MsgBus;
import org.nutz.plugins.zbus.MsgEventHandler;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

@IocBean
public class PushService implements MsgEventHandler<PushPayload> {
	
	@Inject
	protected JPushClient jpush;
	
	@Inject("java:$conf.getBoolean('jpush.enable', true)")
	protected boolean enable;

	public Object call(MsgBus bus, PushPayload event) throws Exception {
		if (!enable)
			return null;
		System.out.println(event.toJSON());
		PushResult re = jpush.sendPush(event);
		return re;
	}

}
