package org.nutz.plugins.zbus;

public interface MsgEventHandler {

	boolean isSupport(Object event);
	
	Object call(MsgBus bus, Object event) throws Exception;
}
