var ioc = {
		conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				paths : ["custom/"]
			}
		},
	    dataSource : {
	        factory : "$conf#make",
	        args : ["com.alibaba.druid.pool.DruidDataSource", "db."],
	        type : "com.alibaba.druid.pool.DruidDataSource",
	        events : {
	        	create : "init",
	            depose : 'close'
	        }
	    },
	    dataSource_slave : { // 为了演示主从, 真正用的话, 下面的"db."改成"db_slave."
	        factory : "$conf#make",
	        args : ["com.alibaba.druid.pool.DruidDataSource", "db."],
	        type : "com.alibaba.druid.pool.DruidDataSource",
	        events : {
	        	create : "init",
	            depose : 'close'
	        }
	    },
		dao : {
			type : "org.nutz.dao.impl.NutDao",
			args : [{refer:"dataSource"}],
			fields : {
				executor : {refer:"cacheExecutor"}
			}
		},
		cacheExecutor : {
			//type : "org.nutz.plugins.cache.dao.CachedNutDaoExecutor",
			type : "net.wendal.nutzbook.util.MasterSlaveDaoExecutor",
			fields : {
				cacheProvider : {refer:"cacheProvider"},
				cachedTableNames : [ 
				    "t_user_profile", "t_user", "t_role",
					"t_permission", "t_role_permission", 
					"t_topic","t_topic_reply", "t_big_content",
					"t_oauth_user", "t_user_role" ],
				slave : {refer:"dataSource_slave"}
		}
	},
	/*
	// 基于内存的简单LRU实现
	cacheProvider : {
		type : "org.nutz.plugins.cache.dao.impl.provider.MemoryDaoCacheProvider",
		fields : {
			cacheSize : 10000 // 缓存的对象数
		},
		events : {
			create : "init"
		}
	}
	 */
	// 基于Ehcache的DaoCacheProvider
	cacheProvider : {
		type : "org.nutz.plugins.cache.dao.impl.provider.EhcacheDaoCacheProvider",
		fields : {
			cacheManager : {
				refer : "cacheManager"
			}
		// 引用ehcache.js中定义的CacheManager
		},
		events : {
			create : "init"
		}
	}
};