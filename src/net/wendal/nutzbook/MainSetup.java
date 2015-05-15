package net.wendal.nutzbook;

import java.util.Date;
import java.util.List;

import net.wendal.nutzbook.bean.FaqItem;
import net.wendal.nutzbook.bean.Permission;
import net.wendal.nutzbook.bean.Role;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.service.FaqService;
import net.wendal.nutzbook.service.PermisssionService;
import net.wendal.nutzbook.service.UserService;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class MainSetup implements Setup {
	
	public void init(NutConfig conf) {
		Ioc ioc = conf.getIoc();
		Dao dao = ioc.get(Dao.class);
		Daos.createTablesInPackage(dao, "net.wendal.nutzbook", false);
		
		// 初始化默认根用户
		User admin = dao.fetch(User.class, "admin");
		if (admin == null) {
			UserService us = ioc.get(UserService.class);
			admin = us.add("admin", "123456");
		}
		
		// 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
		ioc.get(NutQuartzCronJobFactory.class);
		
		PermisssionService ps = ioc.get(PermisssionService.class);
		ps.initFormPackage("net.wendal.nutzbook");
		

		// 检查一下admin的权限
		Role adminRole = dao.fetch(Role.class, "admin");
		if (adminRole == null) {
			adminRole = ps.addRole("admin");
		}
		// admin账号必须存在与admin组
		if (0 == dao.count("t_user_role", Cnd.where("u_id", "=", admin.getId()).and("role_id", "=", adminRole.getId()))) {
			dao.insert("t_user_role", Chain.make("u_id", admin.getId()).add("role_id", adminRole.getId()));
		}
		// admin组必须有authority:* 也就是权限管理相关的权限
		List<Record> res = dao.query("t_role_permission", Cnd.where("role_id", "=", adminRole.getId()));
		OUT: for (Permission permission : dao.query(Permission.class, Cnd.where("name", "like", "authority:%"), null)) {
			for (Record re : res) {
				if (re.getInt("permission_id") == permission.getId())
					continue OUT;
			}
			dao.insert("t_role_permission", Chain.make("role_id", adminRole.getId()).add("permission_id", permission.getId()));
		};
		
		// faq 初始化
		if (dao.count(FaqItem.class) == 0) {
			FaqItem faq = new FaqItem();
			faq.setTitle("nutz官网是什么?");
			faq.setAnswer("http://nutzam.com".getBytes());
			faq.setCreateTime(new Date());
			faq.setUpdateTime(new Date());
			dao.insert(faq);
		}
		
		ioc.get(FaqService.class);
	}
	
	public void destroy(NutConfig conf) {
	}

}
