package org.nutz.dao.mapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.MethodParamNamesScaner;

public abstract class MethodMapper {

	protected Method method;
	
	protected Dao dao;
	
	protected Class<?> returnType;
	
	protected List<String> paramNames;

	public MethodMapper(Dao dao, Method method) {
		super();
		this.dao = dao;
		this.method = method;
		this.returnType = method.getReturnType();
		if (List.class.isAssignableFrom(this.returnType)) {
			this.returnType = (Class<?>) Mirror.me(method.getGenericReturnType()).getGenericsType(0);
		}
		paramNames = mName();
	}
	
	protected List<String> mName() {
		List<String> paramNames = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (char tmp : method.getName().substring(prefix().length()).toCharArray()) {
			if ('A' <= tmp && tmp <= 'Z') {
				if (sb.length() > 0) {
					paramNames.add(sb.toString());
					sb.setLength(0);
				}
			}
			sb.append(Character.toLowerCase(tmp));
		}
		if (sb.length() > 0)
			paramNames.add(sb.toString());
		if (paramNames.isEmpty() && method.getParameterTypes().length != 0) {
			paramNames = MethodParamNamesScaner.getParamNames(method);
		}
		return paramNames;
	}
	
	public Cnd makeCnd(Object[] args) {
		Cnd cnd = Cnd.NEW();
		for (int i = 0; i < args.length; i++) {
			cnd.and(paramNames.get(i), "=", args[i]);
		}
		return cnd;
	}

	public abstract Object exec(Object[] args);
	
	public abstract String prefix();
}
