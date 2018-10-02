
var config_map = {
		"weixin.appid" : "AppID(应用ID)",
		"weixin.appsecret" : "AppSecret(应用密钥)",
		"weixin.token" : "Token(令牌)",
		"weixin.aes" : "EncodingAESKey(消息加解密密钥)"
};
var vueWeixinConfigList = new Vue({
	el : "#weixin_basic_div",
	data : {
		configs : []
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/config/list?prefix=weixin",
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						var configs = [];
						for (var k in config_map) {
							var config = {};
							config.name = k;
							config.display = config_map[k];
							config.value = re.data[k];
							configs.push(config);
						}
						vueWeixinConfigList.configs = configs
					} else if (re && re.msg) {
						layer.alert(re.msg);
					}
				},
				fail : function(err) {
					layer.alert("加载失败:" + err);
				},
				error : function(err) {
					layer.alert("加载失败:" + err);
				}
			});
		},
		do_save : function() {
			var p = {};
			for (var i in this.configs) {
				p[this.configs[i].name] = this.configs[i].value;
			}
			$.ajax({
				url : base + "/admin/config/save?notify=weixinService",
				type : "POST",
				data : p,
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						vueWeixinConfigList.dataReload();
						layer.alert("保存完成");
					} else if (re && re.msg) {
						layer.alert("失败:" + re.msg);
					}
				},
				fail : function(err) {
					layer.alert("加载失败:" + err);
				},
				error : function(err) {
					layer.alert("加载失败:" + err);
				}
			});
		}
	},
	created : function() {
		this.dataReload();
	}
});