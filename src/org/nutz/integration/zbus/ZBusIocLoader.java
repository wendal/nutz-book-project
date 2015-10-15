package org.nutz.integration.zbus;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

/**
 * 这个加载器的工作方式: 查找指定package下的类,看看有无@ZBusExport注解,如果有,登记到zbus去
 * 
 * @author wendal
 *
 */
public class ZBusIocLoader implements IocLoader {
	
	private static final Log log = Logs.get();

	protected Map<String, Class<?>> map = new HashMap<>();

	public ZBusIocLoader(String... pkgs) {
		for (String pkg : pkgs) {
			add(pkg);
		}
	}

	protected void add(String pkg) {
		for (Class<?> klass : Scans.me().scanPackage(pkg)) {
			add(klass);
		}
	}

	protected void add(Class<?> klass) {
		ZBusInvoker export = klass.getAnnotation(ZBusInvoker.class);
		if (export != null) {
			String name = export.value();
			if (Strings.isBlank(name)) {
				name = Strings.lowerFirst(klass.getSimpleName());
			}
			log.debugf("define zbus export bean [%s] as name=%s", klass.getName(), name);
			map.put(name, klass);
		}
	}

	public String[] getName() {
		return map.keySet().toArray(new String[map.size()]);
	}

	public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
		Class<?> klass = map.get(name);
		if (klass == null)
			return null;
		NutMap _map = new NutMap().setv("factory", "$rpc#getService").setv("args", new String[] { klass.getName() });
		return loading.map2iobj(_map);
	}

	@Override
	public boolean has(String name) {
		return map.containsKey(name);
	}

}
