package org.nutz.dao.mapper.impl;

import java.lang.reflect.Method;

import org.nutz.dao.Dao;
import org.nutz.dao.mapper.MethodMapper;

public class FetchMethodMapper extends MethodMapper {

	public FetchMethodMapper(Dao dao, Method method) {
		super(dao, method);
	}

	public String prefix() {
		return "fetchBy";
	}

	public Object exec(Object[] args) {
		return dao.fetch(returnType, makeCnd(args));
	}

}
