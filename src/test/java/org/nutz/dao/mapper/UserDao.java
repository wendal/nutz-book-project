package org.nutz.dao.mapper;

import java.util.List;

import org.nutz.dao.Dao;

import net.wendal.nutzbook.bean.Role;
import net.wendal.nutzbook.bean.User;

public interface UserDao extends Dao {

	User fetchById(int id);
	
	List<User> queryByName(String name);
	
	
	User fetchBy(int id); // 基于局部变量参数表+jdk参数名表
	
	User fetchUserById(int id);
	
	Role fetchRoleById(int id);
}
