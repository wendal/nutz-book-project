package net.wendal.nutzbook.zbus;

import org.nutz.integration.zbus.ZBusInvoker;

@ZBusInvoker
public interface SayHelloWorld {

	public String hi(String name);
}
