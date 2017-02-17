
var config_map = {
		"mail.host" : "smtp服务器域名",
		"mail.port" : "端口号",
		"mail.username" : "用户名",
		"mail.password" : "密码",
		"mail.ssl" : "启用SSL",
		"mail.from" : "发件人邮箱"
};
var vueEmailcConfigList = new Vue({
	el : "#system_mail_div",
	data : {
		configs : [],
		test_mail_addr : "vt400@qq.com"
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/config/list?prefix=mail",
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
						vueEmailcConfigList.configs = configs
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
				url : base + "/admin/config/save",
				type : "POST",
				data : p,
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						vueEmailcConfigList.dataReload();
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
		},
		do_mail_test : function() {
			$.ajax({
				url : base + "/adminlte/test/mail",
				type : "POST",
				data : "to=" + vueEmailcConfigList.test_mail_addr,
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						layer.alert("发送成功");
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