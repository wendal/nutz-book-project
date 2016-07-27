package net.wendal.nutzbook;

import java.nio.charset.Charset;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.plugins.view.freemarker.FreeMarkerConfigurer;
import org.quartz.Scheduler;

import com.alibaba.dubbo.config.ProtocolConfig;

import freemarker.template.Configuration;
import net.sf.ehcache.CacheManager;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.service.AuthorityService;
import net.wendal.nutzbook.service.BigContentService;
import net.wendal.nutzbook.service.DubboWayService;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.service.syslog.SysLogService;
import net.wendal.nutzbook.service.yvr.YvrService;
import net.wendal.nutzbook.util.Markdowns;

/**
 * Nutz内核初始化完成后的操作
 * 
 * @author wendal
 *
 */
public class MainSetup implements Setup {

	private static final Log log = Logs.get();
	
	public static PropertiesProxy conf;

	public void init(NutConfig nc) {
		NutShiro.DefaultLoginURL = "/admin/logout";
		// 检查环境
		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.warn("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}

		// 获取Ioc容器及Dao对象
		Ioc ioc = nc.getIoc();
		// 加载freemarker自定义标签　自定义宏路径
		ioc.get(Configuration.class).setAutoImports(new NutMap().setv("p", "/ftl/pony/index.ftl").setv("s", "/ftl/spring.ftl"));
		ioc.get(FreeMarkerConfigurer.class, "mapTags");
		Dao dao = ioc.get(Dao.class);

		// 为全部标注了@Table的bean建表
		Daos.createTablesInPackage(dao, getClass().getPackage().getName()+".bean", false);
		Daos.migration(dao, Topic.class, true, false);
		Daos.migration(dao, TopicReply.class, true, false);
		
		// 迁移Topic和TopicReply的数据到BigContent
		BigContentService bcs = ioc.get(BigContentService.class);
		for (String topicId : dao.execute(Sqls.queryString("select id from t_topic where cid is null")).getObject(String[].class)) {
			Topic topic = dao.fetch(Topic.class, topicId);
			String cid = bcs.put(topic.getContent());
			topic.setContentId(cid);
			topic.setContent(null);
			dao.update(topic, "(content|contentId)");
		}
		for (String topicId : dao.execute(Sqls.queryString("select id from t_topic_reply where cid is null")).getObject(String[].class)) {
			TopicReply reply = dao.fetch(TopicReply.class, topicId);
			String cid = bcs.put(reply.getContent());
			reply.setContentId(cid);
			reply.setContent(null);
			dao.update(reply, "(content|contentId)");
		}

		// 获取配置对象
		conf = ioc.get(PropertiesProxy.class, "conf");

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
		as.initFormPackage(getClass().getPackage().getName());
		as.checkBasicRoles(admin);

		// 检查一下Ehcache CacheManager 是否正常.
		CacheManager cacheManager = ioc.get(CacheManager.class);
		log.debug("Ehcache CacheManager = " + cacheManager);
		// CachedNutDaoExecutor.DEBUG = true;

		// 设置Markdown缓存
		if (cacheManager.getCache("markdown") == null)
			cacheManager.addCache("markdown");
		Markdowns.cache = cacheManager.getCache("markdown");
		
		if (dao.meta().isMySql()) {
			String schema = dao.execute(Sqls.fetchString("SELECT DATABASE()")).getString();
			
			// 检查所有非日志表,如果表引擎是MyISAM,切换到InnoDB
			Sql sql = Sqls.queryString("SELECT TABLE_NAME FROM information_schema.TABLES where TABLE_SCHEMA = @schema and engine = 'MyISAM'");
			sql.params().set("schema", schema);
			for (String tableName : dao.execute(sql).getObject(String[].class)) {
				if (tableName.startsWith("t_syslog") || tableName.startsWith("t_user_message"))
					continue;
				dao.execute(Sqls.create("alter table "+tableName+" ENGINE = InnoDB"));
			}
		}
		
		ioc.get(YvrService.class).updateTopicTypeCount();
		
		Mvcs.disableFastClassInvoker = false;
		
		// 初始化Dubbo服务
		
		try {
            ioc.get(null, DubboWayService.class.getName());
        }
        catch (Exception e) {
            log.debug("dubbo error", e);
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
		// 解决com.alibaba.druid.proxy.DruidDriver和com.mysql.jdbc.Driver在reload时报warning的问题
		// 多webapp共享mysql驱动的话,以下语句删掉
		Enumeration<Driver> en = DriverManager.getDrivers();
		while (en.hasMoreElements()) {
            try {
                Driver driver = en.nextElement();
                String className = driver.getClass().getName();
                if ("com.alibaba.druid.proxy.DruidDriver".equals(className) 
                     || "com.mysql.jdbc.Driver".equals(className)) {
                    log.debug("deregisterDriver: " + className);
                    DriverManager.deregisterDriver(driver);
                }
            }
            catch (Exception e) {
            }
        }
		

		ProtocolConfig.destroyAll();
	}
}
