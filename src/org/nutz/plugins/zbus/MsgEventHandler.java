package org.nutz.plugins.zbus;

public interface MsgEventHandler<T> {
	
	Object call(MsgBus bus, T event) throws Exception;
}
