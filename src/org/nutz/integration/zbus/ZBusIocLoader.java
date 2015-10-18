package org.nutz.integration.zbus;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.resource.Scans;

/**
 * 这个加载器的工作方式: 查找指定package下的类,看看有无@ZBusExport注解,如果有,登记到zbus去
 * 
 * @author wendal
 *
 */
public class ZBusIocLoader extends JsonLoader {
	
	public ZBusIocLoader(){}

	public ZBusIocLoader(String... pkgs) {
		for (String pkg : pkgs) {
			add(pkg);
		}
		_load("zbus-common.js");
		_load("zbus-rpc-invoker.js");
		_load("zbus-rpc-service.js");
		_load("zbus-server.js");
	}

	protected void add(String pkg) {
		for (Class<?> klass : Scans.me().scanPackage(pkg)) {
			ZBusFactory.addInovker(klass, getMap());
		}
	}

	@SuppressWarnings("unchecked")
	public void _load(String path) {
		InputStream ins = getClass().getClassLoader().getResourceAsStream("ioc/"+path);
		if (ins == null)
			ins = getClass().getResourceAsStream(path);
		if (ins == null)
			return;
		try {
			String s = Lang.readAll(new InputStreamReader(ins));
	        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
	        if (null != map && map.size() > 0)
	            getMap().putAll(map);
		} catch (Exception e) {
		} finally {
			Streams.safeClose(ins);
		}
	}
}
