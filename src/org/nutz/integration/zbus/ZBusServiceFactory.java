package org.nutz.integration.zbus;

import org.nutz.ioc.Ioc;
import org.nutz.resource.Scans;
import org.zbus.rpc.RpcProcessor;

public class ZBusServiceFactory {
	
	public static void build(RpcProcessor rpcProcessor, Ioc ioc, String ...pkgs) {
		for (String pkg : pkgs) {
			for (Class<?> klass : Scans.me().scanPackage(pkg)) {
				ZBusService zBusService = klass.getAnnotation(ZBusService.class);
				if (zBusService != null) {
					rpcProcessor.addModule(ioc.get(klass));
				}
			}
		}
	}
}
