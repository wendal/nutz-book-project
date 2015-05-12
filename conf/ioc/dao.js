var ioc = {
		conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				paths : ["custom/"]
			}
		},
	    dataSource : {
	        type : "com.alibaba.druid.pool.DruidDataSource",
	        events : {
	        	create : "init",
	            depose : 'close'
	        },
	        fields : {
	            url : {java:"$conf.get('db.url')"},
	            username : {java:"$conf.get('db.username')"},
	            password : {java:"$conf.get('db.password')"},
	            testWhileIdle : true,
	            validationQuery : {java:"$conf.get('db.validationQuery')"},
	            maxActive : {java:"$conf.get('db.maxActive')"},
	            filters : "mergeStat",
	            connectionProperties : "druid.stat.slowSqlMillis=2000"
	        }
	    },
		dao : {
			type : "org.nutz.dao.impl.NutDaoExt",
			args : [{refer:"dataSource"}],
			fields : {
				executor : {refer:"cacheExecutor"}
			}
		},
		cacheExecutor : {
			type : "org.nutz.plugins.cache.dao.CachedNutDaoExecutor",
			fields : {
				cacheProvider : {refer:"cacheProvider"},
				cachedTableNames : ["t_user_profile"]
			}
		},
		cacheProvider : {
			type : "org.nutz.plugins.cache.dao.impl.provider.MemoryDaoCacheProvider",
			fields : {
				cacheSize : 10000000
			},
			events : {
				create : "init"
			}
		}
};