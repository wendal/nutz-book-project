var ioc = {
		conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				ignoreResourceNotFound : true,
				paths : ["custom/", "/opt/nutzcn/local.properties"]
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
	    slaveDataSource : {
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
				runner : {refer: "daoRunner"},
				interceptors : [{refer:"cacheExecutor"}, "log", "time"]
			}
		},
		daoRunner : {
			type : "org.nutz.dao.impl.sql.run.NutDaoRunner",
			fields : {
				slaveDataSource : {refer:"slaveDataSource"}
			}
		},
		cacheExecutor : {
			type : "org.nutz.plugins.cache.dao.DaoCacheInterceptor",
			fields : {
				cacheProvider : {refer:"cacheProvider"},
				cachedTableNames : [ 
				    "t_user_profile", "t_user", "t_role",
					"t_permission", "t_role_permission",  "t_permission_category",
					"t_topic_reply", "t_big_content",
					"t_oauth_user", "t_user_role",
					"t_sub_forum"]
		}
	},
	// 基于Ehcache的DaoCacheProvider
	cacheProvider : {
		type : "org.nutz.plugins.cache.dao.impl.provider.EhcacheDaoCacheProvider",
		fields : {
			cacheManager : {
				refer : "cacheManager"
			},
		},
		events : {
			create : "init"
		}
	}
};