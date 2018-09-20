var ioc={
	emailAuthenticator : {
		singleton : false,
		type : "org.apache.commons.mail.DefaultAuthenticator",
		args : [{java:"$conf.get('mail.username')"}, {java:"$conf.get('mail.password')"}]
	},
	htmlEmail : {
		type : "org.apache.commons.mail.ImageHtmlEmail",
		singleton : false,
		fields : {
			hostName : {java:"$conf.get('mail.host')"},
			smtpPort : {java:"$conf.get('mail.port')"},
			authenticator : {refer:"emailAuthenticator"},
			SSLOnConnect : {java:"$conf.get('mail.ssl')"},
			from : {java:"$conf.get('mail.from')"},
			charset : {java:"$conf.get('mail.charset', 'UTF-8')"}
		}
	}	
};