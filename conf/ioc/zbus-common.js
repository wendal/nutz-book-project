var ioc = {
		// zbus 服务信息配置
		brokerConfig : {
			type : "org.zbus.broker.BrokerConfig",
			fields:{
				"serverAddress" : {java:"$conf.get('zbus.serverAddr', '127.0.0.1:15555')"}
			}
		}
};