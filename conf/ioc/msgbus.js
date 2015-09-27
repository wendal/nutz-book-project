var ioc = {
		bus : {
			type : "org.nutz.plugins.zbus.MsgBus",
			fields : {
				pkg : "net.wendal.nutzbook.service",
				ioc : {refer:"$ioc"}
			},
			events : {
				create : "init",
				depose : "close"
			}
		}
};