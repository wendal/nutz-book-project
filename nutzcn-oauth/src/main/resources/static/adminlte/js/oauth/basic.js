
var vueOauthConfigList = new Vue({
	el : "#oauth_basic_div",
	data : {
		configs : [
			{name:"qq", display:"QQ登录", enable:"false", consumer_key:"", consumer_secret:"", custom_permissions:""},
			{name:"api.github.com", display:"Github登录", enable:"false", consumer_key:"", consumer_secret:"", custom_permissions:""}
		]
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/config/list?prefix=oauth",
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						for (var i in vueOauthConfigList.configs) {
							var config = vueOauthConfigList.configs[i];
							config.enable = re.data["oauth."+config.name+".enable"];
							config.consumer_key = re.data["oauth."+config.name+".consumer_key"];
							config.consumer_secret = re.data["oauth."+config.name+".consumer_secret"];
							config.custom_permissions = re.data["oauth."+config.name+".custom_permissions"];
			
						}
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
		do_save : function(name) {
			var p = {};
			console.log(name);
			console.log(JSON.stringify(this.configs));
			for (var i in this.configs) {
				var config = this.configs[i];
				if (config.name != name) {
					continue;
				}
				p["oauth."+config.name+".enable"] = config.enable;
				p["oauth."+config.name+".consumer_key"] = config.consumer_key;
				p["oauth."+config.name+".consumer_secret"] = config.consumer_secret;
				p["oauth."+config.name+".custom_permissions"] = config.custom_permissions;
			}
			$.ajax({
				url : base + "/admin/oauth/save",
				type : "POST",
				data : p,
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						vueOauthConfigList.dataReload();
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