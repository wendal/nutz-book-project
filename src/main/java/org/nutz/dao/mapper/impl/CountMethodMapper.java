package org.nutz.dao.mapper.impl;

import java.lang.reflect.Method;

import org.nutz.dao.Dao;
import org.nutz.dao.mapper.MethodMapper;

public class CountMethodMapper extends MethodMapper {

	public CountMethodMapper(Dao dao, Method method, String pName) {
		super(dao, method, pName);
	}

	public Object exec(Object[] args) {
		return dao.count(pojoType, makeCnd(args));
	}

	public String prefix() {
		return "count";
	}

}
