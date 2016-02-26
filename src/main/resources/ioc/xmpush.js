var ioc = {
		// 小米推送
		mxpush : {
			type : "com.xiaomi.xmpush.server.Sender",
			args : [
			        {java: "$conf.get('xmpush.appSecret', '')"}
			        ]
		}
};