package org.nutz.aop.interceptor.async;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.SimpleAopMaker;

public class AsyncAopIocLoader extends SimpleAopMaker<Async>{
	
	protected int size;
	
	protected ExecutorService es;
	
	public AsyncAopIocLoader(){
		this(32);
	}
	
	public AsyncAopIocLoader(int size) {
		this.size = size;
		es = Executors.newFixedThreadPool(size);
		System.out.println("self=" + getName()[0]);
	}
	
	public MethodInterceptor makeIt(Async async, Method method) {
		return new AsyncMethodInterceptor(method, async, es);
	}
	
	public void depose() throws Exception {
		if (es != null)
			es.shutdownNow();
	}
}
