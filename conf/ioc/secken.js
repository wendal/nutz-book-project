var ioc = {
		secken : {
			type : "org.nutz.auth.secken.Secken",
			args : [
			        {java:"$conf.get('secken.appid')"},
			        {java:"$conf.get('secken.appkey')"},
			        {java:"$conf.get('secken.authid')"},
			        ]
		}	
};