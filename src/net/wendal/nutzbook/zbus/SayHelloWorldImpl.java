package net.wendal.nutzbook.zbus;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class SayHelloWorldImpl implements SayHelloWorld{

	public String hi(String name) {
		return "hi,"+name;
	}

}
