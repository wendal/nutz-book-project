package net.wendal.nutzbook;

import java.util.Date;

import net.sf.ehcache.CacheManager;
import net.wendal.nutzbook.bean.FaqItem;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.service.FaqService;
import net.wendal.nutzbook.service.AuthorityService;
import net.wendal.nutzbook.service.UserService;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.snaker.engine.SnakerEngine;

public class MainSetup implements Setup {
	
	private static final Log log = Logs.get();
	
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
		
		AuthorityService as = ioc.get(AuthorityService.class);
		as.initFormPackage("net.wendal.nutzbook");
		as.checkBasicRoles(admin);

		
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
		
		// 检查一下Ehcache CacheManager 是否正常.
		CacheManager cacheManager = ioc.get(CacheManager.class);
		log.debug("Ehcache CacheManager = " + cacheManager);
		//CachedNutDaoExecutor.DEBUG = true;
		
		SnakerEngine snakerEngine = ioc.get(SnakerEngine.class);
		log.info("snakerflow init complete == " + snakerEngine);
	}
	
	public void destroy(NutConfig conf) {
	}

}
