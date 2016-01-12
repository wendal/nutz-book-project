package org.nutz.dao.mapper;

import java.util.List;

import net.wendal.nutzbook.bean.User;

public interface UserMapper {

	User fetchById(int id);
	
	List<User> queryByName(String name);
}
