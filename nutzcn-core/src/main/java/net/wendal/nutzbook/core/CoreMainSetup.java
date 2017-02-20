package net.wendal.nutzbook.core;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.el.opt.custom.CustomMake;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.plugins.slog.service.SlogService;
import org.quartz.Scheduler;

import net.wendal.nutzbook.core.bean.Role;
import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.bean.UserProfile;
import net.wendal.nutzbook.core.ig.RedisIdGenerator;
import net.wendal.nutzbook.core.service.AuthorityService;
import net.wendal.nutzbook.core.service.ConfigureService;
import net.wendal.nutzbook.core.service.UserService;

/**
 * Nutz内核初始化完成后的操作
 * 
 * @author wendal
 *
 */
public class CoreMainSetup implements Setup {

	private static final Log log = Logs.get();
	
	public static PropertiesProxy conf;

    public void init(NutConfig nc) {
		// 检查环境,必须运行在UTF-8环境
		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.error("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}
		// Log4j 2.x的JMX默认启用,会导致reload时内存不释放!!
		if (!"true".equals(System.getProperty("log4j2.disable.jmx")))
		    log.error("log4j2 jmx will case reload memory leak! pls add -Dlog4j2.disable.jmx=true to JAVA_OPTS");

		// 获取Ioc容器及Dao对象
		Ioc ioc = nc.getIoc();

		// 初始化JedisAgent
		//JedisAgent jedisAgent = ioc.get(JedisAgent.class);

        Dao dao = ioc.get(Dao.class);
        
        // 为全部标注了@Table的bean建表
        Daos.createTablesInPackage(dao, getClass().getPackage().getName(), false);
        Daos.migration(dao, User.class, true, false);
        Daos.migration(dao, Role.class, true, false);

		// 初始化redis实现的id生成器
		CustomMake.me().register("ig", ioc.get(RedisIdGenerator.class));

		// 获取配置对象
		conf = ioc.get(PropertiesProxy.class, "conf");
		ioc.get(ConfigureService.class).doReload();

		// 初始化SysLog,触发全局系统日志初始化
		ioc.get(SlogService.class).log("method", "system", null, "系统启动", false);

		// 初始化默认根用户
        UserService us = ioc.get(UserService.class);
		User admin = dao.fetch(User.class, "admin");
		if (admin == null) {
			admin = us.add("admin", "123456");
		}
		// 初始化游客用户
		User guest = dao.fetch(User.class, "guest");
		if (guest == null) {
			guest = us.add("guest", "123456");
			UserProfile profile = dao.fetch(UserProfile.class, guest.getId());
			profile.setNickname("游客");
			dao.update(profile, "nickname");
		}

		// 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
		ioc.get(NutQuartzCronJobFactory.class);

		// 权限系统初始化
		AuthorityService as = ioc.get(AuthorityService.class);
		as.initFormPackage(getClass().getPackage().getName());
		as.checkBasicRoles();

		Mvcs.disableFastClassInvoker = false;
	}

	public void destroy(NutConfig conf) {
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
		// 解决com.alibaba.druid.proxy.DruidDriver和com.mysql.jdbc.Driver在reload时报warning的问题
		// 多webapp共享mysql驱动的话,以下语句删掉
		Enumeration<Driver> en = DriverManager.getDrivers();
		while (en.hasMoreElements()) {
            try {
                Driver driver = en.nextElement();
                String className = driver.getClass().getName();
                log.debug("deregisterDriver: " + className);
                DriverManager.deregisterDriver(driver);
            }
            catch (Exception e) {
            }
        }
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName("com.alibaba.druid:type=MockDriver");
            if (mbeanServer.isRegistered(objectName))
                mbeanServer.unregisterMBean(objectName);
            objectName = new ObjectName("com.alibaba.druid:type=DruidDriver");
            if (mbeanServer.isRegistered(objectName))
                mbeanServer.unregisterMBean(objectName);
        } catch (Exception ex) {
        }
	}
}
