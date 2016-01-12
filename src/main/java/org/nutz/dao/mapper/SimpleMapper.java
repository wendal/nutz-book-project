package org.nutz.dao.mapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.dao.mapper.impl.FetchMethodMapper;
import org.nutz.dao.mapper.impl.QueryMethodMapper;

public class SimpleMapper implements InvocationHandler {
	
	public Dao dao;
	
	public Map<Method, MethodMapper> mappers;
	
	public SimpleMapper(Dao dao, Map<Method, MethodMapper> mappers) {
		this.dao = dao;
		this.mappers = mappers;
	}

	@SuppressWarnings("unchecked")
	public static <T> T map(Dao dao, Class<T> klass) {
		Map<Method, MethodMapper> mappers = new HashMap<>();
		for(Method method : klass.getMethods()) {
			String name = method.getName();
			if (name.startsWith("queryBy")) {
				mappers.put(method, new QueryMethodMapper(dao, method));
			}
			if (name.startsWith("fetchBy")) {
				mappers.put(method, new FetchMethodMapper(dao, method));
			}
		}
		return (T) Proxy.newProxyInstance(klass.getClassLoader(), new Class<?>[]{klass}, new SimpleMapper(dao, mappers));
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodMapper mapper = mappers.get(method);
		if (mapper == null)
			return null;
		return mapper.exec(args);
	}
}
