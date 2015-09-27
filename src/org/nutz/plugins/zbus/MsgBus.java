package org.nutz.plugins.zbus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.log.Logs;
import org.nutz.log.Log;
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
	
	protected Object _call(Object event) throws Exception {
		for (MsgEventHandler handler : handlers) {
			if (handler.isSupport(event)) {
				return handler.call(this, event);
			}
		}
		log.info("no handler obj class=" + event.getClass());
		return null;
	}
	
	public void init() {
		if (threadCount < 1)
			threadCount = 16;
		if (handlers == null) {
			handlers = new ArrayList<MsgEventHandler>();
		}
		if (!Strings.isBlank(pkg)) {
			for (Class<?> klass: Scans.me().scanPackage(pkg)) {
				if (MsgEventHandler.class.isAssignableFrom(klass)) {
					handlers.add((MsgEventHandler) ioc.get(klass));
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
	
	protected List<MsgEventHandler> handlers;
	
	protected int threadCount;
	
	protected String pkg;
	
	protected Ioc ioc;
	
	private static final Log log = Logs.get();
}
