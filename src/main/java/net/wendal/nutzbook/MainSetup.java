package net.wendal.nutzbook;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Enumeration;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.net.ssl.SSLContext;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.el.opt.custom.CustomMake;
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
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import freemarker.template.Configuration;
import net.sf.ehcache.CacheManager;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.msg.UserMessage;
import net.wendal.nutzbook.bean.yvr.SubForum;
import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.ig.RedisIdGenerator;
import net.wendal.nutzbook.mailsrv.SmtpMailListener;
import net.wendal.nutzbook.service.AuthorityService;
import net.wendal.nutzbook.service.BigContentService;
import net.wendal.nutzbook.service.SysConfigureService;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.service.syslog.SysLogService;
import net.wendal.nutzbook.service.yvr.YvrService;
import net.wendal.nutzbook.shiro.cache.LCacheManager;
import net.wendal.nutzbook.shiro.cache.RedisCache;
import net.wendal.nutzbook.util.Markdowns;
import net.wendal.nutzbook.util.RedisKey;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Nutz内核初始化完成后的操作
 * 
 * @author wendal
 *
 */
public class MainSetup implements Setup {

	private static final Log log = Logs.get();
	
	public static PropertiesProxy conf;
	
	protected SMTPServer smtpServer;

	public void init(NutConfig nc) {
		NutShiro.DefaultLoginURL = "/admin/logout";
		// 检查环境,必须运行在UTF-8环境
		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.error("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}
		// Log4j 2.x的JMX默认启用,会导致reload时内存不释放!!
		if (!"true".equals(System.getProperty("log4j2.disable.jmx")))
		    log.error("log4j2 jmx will case reload memory leak! pls add -Dlog4j2.disable.jmx=true to JAVA_OPTS");

		// 获取Ioc容器及Dao对象
		Ioc ioc = nc.getIoc();

		// 初始化RedisCacheManager
		LCacheManager.me().setupJedisPool(ioc.get(JedisPool.class));
		RedisCache.DEBUG = true;

        Dao dao = ioc.get(Dao.class);
        
        // 为全部标注了@Table的bean建表
        Daos.createTablesInPackage(dao, getClass().getPackage().getName()+".bean", false);
        Daos.migration(dao, Topic.class, true, false);
        Daos.migration(dao, TopicReply.class, true, false);

		JedisPool pool = ioc.get(JedisPool.class);
		try (Jedis jedis = pool.getResource()) {
            if (!jedis.exists("ig:t_user") && dao.count(User.class) > 0)
                jedis.set("ig:t_user", ""+dao.getMaxId(User.class)+1);
            if (!jedis.exists("ig:t_user_message") && dao.count(UserMessage.class) > 0)
                jedis.set("ig:t_user_message", ""+dao.getMaxId(UserMessage.class)+1);
		}
		// 初始化redis实现的id生成器
		CustomMake.me().register("ig", ioc.get(RedisIdGenerator.class));

		// 加载freemarker自定义标签　自定义宏路径
		ioc.get(Configuration.class).setAutoImports(new NutMap().setv("p", "/ftl/pony/index.ftl").setv("s", "/ftl/spring.ftl"));
		ioc.get(FreeMarkerConfigurer.class, "mapTags");

		
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
		// 初始化子论坛数据
		if (dao.count(SubForum.class) == 0) {
		    SubForum fireflow = new SubForum();
		    fireflow.setDisplay("Fireflow工作流");
		    fireflow.setTagname("fireflow");
		    fireflow.setMasters(Arrays.asList("qq_9d0c01e6", "fireflow"));
		    dao.insert(fireflow);
		    
		    SubForum ssdb = new SubForum();
		    ssdb.setDisplay("SSDB数据库");
		    ssdb.setTagname("ssdb");
		    ssdb.setMasters(Arrays.asList("ideawu"));
		    dao.insert(ssdb);
		    
		    SubForum nginx = new SubForum();
		    nginx.setDisplay("Nginx服务器");
		    nginx.setTagname("nginx");
		    dao.insert(nginx);
		}

		// 获取配置对象
		conf = ioc.get(PropertiesProxy.class, "conf");
		ioc.get(SysConfigureService.class).doReload();

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

        try (final Jedis jedis = pool.getResource()) {
            dao.each(Topic.class, Cnd.where("good", "=", true), (index, topic, length) -> {
                Double t = jedis.zscore(RedisKey.RKEY_TOPIC_UPDATE
                                        + topic.getType(),
                                        topic.getId());
                if (t == null)
                    t = (double) topic.getCreateTime().getTime();
                jedis.zadd(RedisKey.RKEY_TOPIC_UPDATE + "good", t, topic.getId());
            });
        }
        
		ioc.get(YvrService.class).updateTopicTypeCount();
		
		Mvcs.disableFastClassInvoker = false;
		
		smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(ioc.get(SmtpMailListener.class)));
		smtpServer.start();
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
		
        LCacheManager.me().depose();
        
        // org.brickred.socialauth.util.HttpUtil 把一个内部类注册到SSLContext,擦!
        try {
            SSLContext.getDefault().init(null, null, new SecureRandom());
        }
        catch (Exception e) {
        }
        
        if (smtpServer != null)
            smtpServer.stop();
	}
}
