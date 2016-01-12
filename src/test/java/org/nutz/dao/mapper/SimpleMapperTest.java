package org.nutz.dao.mapper;

import java.util.List;

import org.junit.Test;
import org.nutz.dao.Dao;

import net.wendal.nutzbook.TestBase;
import net.wendal.nutzbook.bean.User;

public class SimpleMapperTest extends TestBase {

	@Test
	public void testMap() {
		// UserMapper 只是个接口
		UserMapper us = SimpleMapper.map(ioc.get(Dao.class), UserMapper.class);
		
		User user = us.fetchById(1);
		assertNotNull(user);
		
		List<User> list = us.queryByName("admin");
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}

}
