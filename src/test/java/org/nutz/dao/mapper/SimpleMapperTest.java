package org.nutz.dao.mapper;

import java.util.List;

import org.junit.Test;
import org.nutz.dao.Dao;

import net.wendal.nutzbook.TestBase;
import net.wendal.nutzbook.bean.User;

public class SimpleMapperTest extends TestBase {

	@Test
	public void testMap() {
		// UserDao 只是个接口
		UserDao us = SimpleMapper.map(ioc.get(Dao.class), User.class.getPackage().getName(), UserDao.class);
		
		User user = us.fetchById(1);
		assertNotNull(user);
		
		List<User> list = us.queryByName("admin");
		assertNotNull(list);
		assertTrue(list.size() > 0);

		assertEquals(user.getName(), us.fetchBy(1).getName());
		assertEquals(user.getName(), us.fetchUserById(1).getName());
		
		
		assertNotNull(us.fetchRoleById(1));
		
		assertTrue(us.count(User.class) > 0);
	}

}
