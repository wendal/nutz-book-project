package net.wendal.nutzbook;

import java.nio.charset.Charset;
import java.util.HashMap;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.plugins.view.freemarker.FreeMarkerConfigurer;
import org.quartz.Scheduler;

import freemarker.template.Configuration;
import net.sf.ehcache.CacheManager;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.beetl.MarkdownFunction;
import net.wendal.nutzbook.service.AuthorityService;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.service.syslog.SysLogService;
import net.wendal.nutzbook.util.Markdowns;

/**
 * Nutz内核初始化完成后的操作
 * 
 * @author wendal
 *
 */
public class MainSetup implements Setup {

	private static final Log log = Logs.get();

	@SuppressWarnings("serial")
	public void init(NutConfig nc) {
		NutShiro.DefaultLoginURL = "/admin/logout";
		// 检查环境
		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.warn("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}

		// 获取Ioc容器及Dao对象
		Ioc ioc = nc.getIoc();
		// 加载freemarker自定义标签　自定义宏路径
		ioc.get(Configuration.class).setAutoImports(new HashMap<String, String>(2) {
			{
				put("p", "/ftl/pony/index.ftl");
				put("s", "/ftl/spring.ftl");
			}
		});
		ioc.get(FreeMarkerConfigurer.class, "mapTags");
		Dao dao = ioc.get(Dao.class);

		// 为全部标注了@Table的bean建表
		Daos.createTablesInPackage(dao, getClass().getPackage().getName()+".bean", false);

		// 获取配置对象
		PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");

		// 初始化SysLog,触发全局系统日志初始化
		ioc.get(SysLogService.class);

		// 初始化默认根用户
		User admin = dao.fetch(User.class, "admin");
		if (admin == null) {
			UserService us = ioc.get(UserService.class);
			admin = us.add("admin", "123456");
		}
		// 初始化游客用户
		User guest = dao.fetch(User.class, "guest");
		if (guest == null) {
			UserService us = ioc.get(UserService.class);
			guest = us.add("guest", "123456");
			UserProfile profile = dao.fetch(UserProfile.class, guest.getId());
			profile.setNickname("游客");
			dao.update(profile, "nickname");
		}

		// 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
		ioc.get(NutQuartzCronJobFactory.class);

		// 权限系统初始化
		AuthorityService as = ioc.get(AuthorityService.class);
		as.initFormPackage("net.wendal.nutzbook");
		as.checkBasicRoles(admin);

		// 检查一下Ehcache CacheManager 是否正常.
		CacheManager cacheManager = ioc.get(CacheManager.class);
		log.debug("Ehcache CacheManager = " + cacheManager);
		// CachedNutDaoExecutor.DEBUG = true;

		// 启用FastClass执行入口方法
		Mvcs.disableFastClassInvoker = false;

		// 设置Markdown缓存
		if (cacheManager.getCache("markdown") == null)
			cacheManager.addCache("markdown");
		Markdowns.cache = cacheManager.getCache("markdown");
		if (conf.getBoolean("cdn.enable", false) && !Strings.isBlank(conf.get("cdn.urlbase"))) {
			MarkdownFunction.cdnbase = conf.get("cdn.urlbase");
		}
		
	}

	public void destroy(NutConfig conf) {
		Markdowns.cache = null;
		// 非mysql数据库,或多webapp共享mysql驱动的话,以下语句删掉
		try {
			Mirror.me(Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread")).invoke(null, "shutdown");
		} catch (Throwable e) {
		}
		// 解决quartz有时候无法停止的问题
		try {
			conf.getIoc().get(Scheduler.class).shutdown(true);
		} catch (Exception e) {
		}
	}
}
