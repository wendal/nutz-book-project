package net.wendal.nutzbook.zbus;

import org.nutz.integration.zbus.annotation.ZBusInvoker;

@ZBusInvoker
public interface SayHelloWorld {

	public String hi(String name);
}
