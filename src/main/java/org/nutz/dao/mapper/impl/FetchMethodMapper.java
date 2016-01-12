package org.nutz.dao.mapper.impl;

import java.lang.reflect.Method;

import org.nutz.dao.Dao;
import org.nutz.dao.mapper.MethodMapper;

public class FetchMethodMapper extends MethodMapper {

	public FetchMethodMapper(Dao dao, Method method, String pName) {
		super(dao, method, pName);
	}

	public String prefix() {
		return "fetch";
	}

	public Object exec(Object[] args) {
		return dao.fetch(pojoType, makeCnd(args));
	}

}
