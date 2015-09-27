package org.nutz.plugins.zbus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

public class MsgBus {
	
	public <T> Future<T> add(Callable<T> task) {
		return es.submit(task);
	}
	
	public Future<Object> event(final Object obj) {
		if (obj == null)
			return null;
		return add(new Callable<Object>() {
			public Object call() throws Exception {
				return _call(obj);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected <T> Object _call(Object event) throws Exception {
		MsgEventHandler<T> handler = (MsgEventHandler<T>) handlers.get(event.getClass().getName());
		if (handler != null)
			return handler.call(this, (T)event);
		log.info("no handler obj class=" + event.getClass());
		return null;
	}
	
	public void init() {
		if (threadCount < 1)
			threadCount = 16;
		if (handlers == null) {
			handlers = new HashMap<String, MsgEventHandler<?>>();
		}
		if (!Strings.isBlank(pkg)) {
			for (Class<?> klass: Scans.me().scanPackage(pkg)) {
				for (Type t : klass.getGenericInterfaces()) {
					if (t instanceof ParameterizedType) {
						ParameterizedType pt = (ParameterizedType)t;
						if (pt.getTypeName().startsWith(MsgEventHandler.class.getName())) {
							String name = ((ParameterizedType)t).getActualTypeArguments()[0].getTypeName();
							log.debugf("add event handler [%s -- %s]", name, klass);
							handlers.put(name, (MsgEventHandler<?>) ioc.get(klass));
						}
					}
				}
			}
		}
		es = Executors.newFixedThreadPool(threadCount);
	}
	
	public void close() throws InterruptedException {
		if (es == null) {
			return;
		}
		es.shutdown();
		es.awaitTermination(5, TimeUnit.SECONDS);
		es = null;
	}
	


	protected ExecutorService es;
	
	protected Map<String, MsgEventHandler<?>> handlers;
	
	protected int threadCount;
	
	protected String pkg;
	
	protected Ioc ioc;
	
	private static final Log log = Logs.get();
}
