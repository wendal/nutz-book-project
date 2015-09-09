package net.wendal.nutzbook;

import java.sql.Connection;

import net.sf.ehcache.CacheManager;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.service.AuthorityService;
import net.wendal.nutzbook.service.RedisService;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.service.socketio.SocketioService;
import net.wendal.nutzbook.service.syslog.SysLogService;
import net.wendal.nutzbook.snakerflow.NutzbookAccessStrategy;
import net.wendal.nutzbook.snakerflow.SnakerEmailInterceptor;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.quartz.Scheduler;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.core.ServiceContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.druid.pool.DruidPooledConnection;

public class MainSetup implements Setup {
	
	private static final Log log = Logs.get();
	
	public void init(NutConfig conf) {
		Ioc ioc = conf.getIoc();
		Dao dao = ioc.get(Dao.class);
		Daos.createTablesInPackage(dao, "net.wendal.nutzbook", false);
		
		// 初始化SysLog
		ioc.get(SysLogService.class);
		
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
		
		//ioc.get(TopicService.class);
		
		// 检查一下Ehcache CacheManager 是否正常.
		CacheManager cacheManager = ioc.get(CacheManager.class);
		log.debug("Ehcache CacheManager = " + cacheManager);
		//CachedNutDaoExecutor.DEBUG = true;
		
		SnakerEngine snakerEngine = ioc.get(SnakerEngine.class);
		// 塞点对象进去
		ServiceContext.put("NutzbookAccessStrategy", ioc.get(NutzbookAccessStrategy.class));
		ServiceContext.put("NutDao", dao);
		ServiceContext.put("nutz-email", ioc.get(SnakerEmailInterceptor.class));
		log.info("snakerflow init complete == " + snakerEngine);
		
		Mvcs.disableFastClassInvoker = false;
		
		// 测试一下能不能拿到原生连接对象
		dao.run(new ConnCallback() {
			public void invoke(Connection conn) throws Exception {
				if (conn instanceof DruidPooledConnection) {
					conn = ((DruidPooledConnection)conn).getConnection();
				}
				if (conn instanceof com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl) {
					conn = ((com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl)conn).getConnectionRaw();
				}
				log.debug("Source Connection Class=" + conn.getClass().getName());
			}
		});
		
		try {
			// redis测试
			JedisPool jedisPool = ioc.get(JedisPool.class);
			try (Jedis jedis = jedisPool.getResource()) {
				String re = jedis.set("_nutzbook_test_key", "http://nutzbook.wendal.net");
				log.debug("redis say : " + re);
				re = jedis.get("_nutzbook_test_key");
				log.debug("redis say : " + re);
			} finally {}
			
			RedisService redis = ioc.get(RedisService.class);
			redis.set("hi", "wendal");
			log.debug("redis say again : "  + redis.get("hi"));
		} catch (Throwable e) {
			log.warn("redis connection fail? it is ok, just for demo now");
		}
		
		ioc.get(SocketioService.class);
	}
	
	public void destroy(NutConfig conf) {
		// 非mysql数据库,或多webapp共享mysql驱动的话,以下语句删掉
		try {
			Mirror.me(Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread")).invoke(null, "shutdown");
		} catch (Throwable e) {
		}
		try {
			conf.getIoc().get(Scheduler.class).shutdown(true);
		} catch (Exception e) {
		}
	}

}
