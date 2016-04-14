var ioc = {
		// 极光推送
		jpush : {
			type : "cn.jpush.api.JPushClient",
			args : [
			        {java: "$conf.get('jpush.masterSecret')"},
			        {java: "$conf.get('jpush.appKey')"}
			        ]
		}
};