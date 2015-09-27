var ioc = {
		jpush : {
			type : "cn.jpush.api.JPushClient",
			args : [
			        {java: "$conf.get('jpush.masterSecret')"},
			        {java: "$conf.get('jpush.appKey')"}
			        ]
		}
};