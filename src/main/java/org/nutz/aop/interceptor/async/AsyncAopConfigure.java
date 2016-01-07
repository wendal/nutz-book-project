package org.nutz.aop.interceptor.async;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.aop.matcher.SimpleMethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.lang.Mirror;

public class AsyncAopConfigure implements AopConfigration, Closeable {
	
	ExecutorService es;
	
	public AsyncAopConfigure(int size) {
		es = Executors.newFixedThreadPool(size);
	}
	
	public AsyncAopConfigure(ExecutorService es) {
		this.es = es;
	}

	public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz) {
		Mirror<?> mirror = Mirror.me(clazz);
		List<InterceptorPair> list = new ArrayList<InterceptorPair>();
		Async parent = clazz.getAnnotation(Async.class);
		for (Method method : mirror.getMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}
			Async async = method.getAnnotation(Async.class);
			// 类没有,方法没有,byebye咯
			if (parent == null && async == null)
				continue;
			if (async == null) {
				async = parent;
			} else {
				parent = async;
			}
			if (!async.enable())
				continue;
			InterceptorPair pair = new InterceptorPair(new AsyncMethodInterceptor(method, async, es), new SimpleMethodMatcher(method));
			list.add(pair);
		}
		return list.isEmpty() ? null : list;
	}

	public void close() throws IOException {
		if (es != null)
			es.shutdownNow();
	}

}
