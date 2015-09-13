package net.wendal.nutzbook.beetl;

import net.sf.ehcache.Element;

import org.beetl.core.cache.Cache;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class BeetlEhcache implements Cache {

	public static net.sf.ehcache.Cache cache;
	
	private static final Log log = Logs.get();
	
	public BeetlEhcache() {
		log.info("init by " + cache);
	}
	
	public Object get(String key) {
		Element ele = cache.get(key);
		if (ele != null)
			return ele.getObjectValue();
		return null;
	}

	public void remove(String key) {
		cache.remove(key);
	}

	public void set(String key, Object value) {
		cache.put(new Element(key, value));
	}

	public void clearAll() {
		cache.removeAll();
	}

}
