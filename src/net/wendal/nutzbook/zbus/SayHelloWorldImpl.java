package net.wendal.nutzbook.zbus;

import org.nutz.integration.zbus.annotation.ZBusService;
import org.nutz.ioc.loader.annotation.IocBean;

@ZBusService
@IocBean
public class SayHelloWorldImpl implements SayHelloWorld{

	public String hi(String name) {
		return "hi,"+name;
	}

}
