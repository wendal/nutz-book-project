package net.wendal.nutzbook;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.service.UserService;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class MainSetup implements Setup {

	public void init(NutConfig conf) {
		Ioc ioc = conf.getIoc();
		Dao dao = ioc.get(Dao.class);
		Daos.createTablesInPackage(dao, "net.wendal.nutzbook", true);
		
		// 初始化默认根用户
		if (dao.count(User.class) == 0) {
			UserService us = ioc.get(UserService.class);
			us.add("admin", "123456");
		}
		
		// 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
		ioc.get(NutQuartzCronJobFactory.class);
	}
	
	public void destroy(NutConfig conf) {
	}

}
