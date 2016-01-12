package org.nutz.dao.mapper.impl;

import java.lang.reflect.Method;

import org.nutz.dao.Dao;
import org.nutz.dao.mapper.MethodMapper;

public class QueryMethodMapper extends MethodMapper {

	public QueryMethodMapper(Dao dao, Method method) {
		super(dao, method);
	}

	public Object exec(Object[] args) {
		return dao.query(returnType, makeCnd(args));
	}
	
	public String prefix() {
		return "queryBy";
	}
}
