package net.wendal.nutzbook;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.integration.zbus.ZBusFactory;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.quartz.Scheduler;
import org.zbus.mq.server.MqServer;
import org.zbus.rpc.RpcProcessor;
import org.zbus.rpc.mq.Service;

import com.alibaba.druid.pool.DruidPooledConnection;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import net.sf.ehcache.CacheManager;
import net.wendal.nutzbook.bean.OAuthUser;
import net.wendal.nutzbook.bean.Permission;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.yvr.TopicReply;
import net.wendal.nutzbook.service.AuthorityService;
import net.wendal.nutzbook.service.RedisService;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.service.socketio.SocketioService;
import net.wendal.nutzbook.service.syslog.SysLogService;
import net.wendal.nutzbook.util.Markdowns;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Nutz内核初始化完成后的操作
 * @author wendal
 *
 */
public class MainSetup implements Setup {
	
	private static final Log log = Logs.get();
	
	public void init(NutConfig nc) {
		NutShiro.DefaultLoginURL="/admin/logout";
		// 检查环境
		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.warn("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}
		if (System.getProperty("ehcache.disk.store.dir") == null) {
			log.info("You shall set up environment variable [ehcache.disk.store.dir], which using at ehcache.xml =>>  -Dehcache.disk.store.dir=/tmp");
		}
		
		// netty的东西,强制让它使用log4j记录日志. 因为环境中存在slf4j,它会自动选用,导致log4j配置日志级别失效
		InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
		
		// 获取Ioc容器及Dao对象
		Ioc ioc = nc.getIoc();
		Dao dao = ioc.get(Dao.class);
		
		// 为全部标注了@Table的bean建表
		Daos.createTablesInPackage(dao, "net.wendal.nutzbook", false);
		// 修正表结构
		Daos.migration(dao, UserProfile.class, true, false);
		Daos.migration(dao, TopicReply.class, true, false);
		Daos.migration(dao, OAuthUser.class, true, false);
		Daos.migration(dao, Permission.class, true, false);
		
		// 获取配置对象
		PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");
		
		// 启动zbus######################
		// 启动内置zbus服务器
		if (conf.getBoolean("zbus.server.embed.enable", true)) {
			ioc.get(MqServer.class);
		}
		// 启动RPC服务端
		if (conf.getBoolean("zbus.rpc.service.enable", true)) {
			RpcProcessor rpcProcessor = ioc.get(RpcProcessor.class);
			ZBusFactory.buildServices(rpcProcessor, ioc, getClass().getPackage().getName());
			ioc.get(Service.class, "rpcService"); // 注意, Service与服务器连接是异步操作
		}
		// 启动 生产者/消费者
		ioc.get(ZBusFactory.class, "zbus");
		// END zbus ######################
		
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
		// 修正没有设置loginname的UserProfile
		List<UserProfile> profiles = Daos.ext(dao, FieldFilter.create(UserProfile.class, "userId")).query(UserProfile.class, Cnd.where("loginname", "=", null));
		for (UserProfile profile : profiles) {
			User user = dao.fetch(User.class, profile.getUserId());
			if (user == null)
				dao.delete(UserProfile.class, profile.getUserId());
			else {
				dao.update(UserProfile.class, Chain.make("loginname", user.getName()), Cnd.where("userId", "=", user.getId()));
				if (profile.getCreateTime() == null || profile.getUpdateAt() == null) {
					if (profile.getCreateTime() == null)
						profile.setCreateTime(user.getCreateTime());
					if (profile.getUpdateTime() == null)
						profile.setUpdateTime(user.getUpdateTime());
					dao.update(profile, "createTime|updateTime");
				}
			}
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
		//CachedNutDaoExecutor.DEBUG = true;
		
		// 启用FastClass执行入口方法
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
		
		// 测试Redis是否正常
		if (conf.getBoolean("redis.enable", false)) {
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
		}
		
		// 启动socketio相关的服务
		if (conf.getBoolean("socketio.enable", false))
			ioc.get(SocketioService.class);

		// 设置Markdown缓存
		if (cacheManager.getCache("markdown") == null)
			cacheManager.addCache("markdown");
		Markdowns.cache = cacheManager.getCache("markdown");
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
