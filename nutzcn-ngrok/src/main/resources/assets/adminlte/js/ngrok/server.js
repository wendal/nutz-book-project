
var config_map = {
		"ngrok.server.srv_host" : "服务器域名",
		"ngrok.server.srv_port" : "服务器端口",
		"ngrok.client.http_port" : "Http服务端口",
};
var vueNgrokClient = new Vue({
	el : "#nginx_server_div",
	data : {
		configs : [],
		client_status : {}
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/config/list?prefix=ngrok.server",
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
						vueNgrokClient.configs = configs
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
				url : base + "/admin/config/save?notify=ngrokClientHolder",
				type : "POST",
				data : p,
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						layer.alert("保存完成");
						vueNgrokClient.dataReload();
						vueNgrokClient.status_reload();
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
		do_start : function() {
			$.ajax({
				url : base + "/admin/ngrok/client/start",
				type : "POST",
				dataType : "json",
				success : function(re) {
					if (re && re.ok) {
						layer.alert("启动中");
					} else if (re && re.msg) {
						layer.alert("失败:" + re.msg);
					}
					vueNgrokClient.status_reload();
				},
			});
		},
		do_stop : function() {
			$.ajax({
				url : base + "/admin/ngrok/client/stop",
				type : "POST",
				dataType : "json",
				success : function(re) {
					if (re && re.ok) {
						layer.alert("停止中");
					} else if (re && re.msg) {
						layer.alert("失败:" + re.msg);
					}
					vueNgrokClient.status_reload();
				},
			});
		},
		status_reload : function() {
			$.ajax({
				url : base + "/admin/ngrok/client/status",
				dataType : "json",
				success : function(re) {
					if (re && re.ok) {
						vueNgrokClient.client_status = re.data;
					} else if (re && re.msg) {
						layer.alert("加载状态失败:" + re.msg);
					}
				},
			});
		}
	},
	created : function() {
		this.dataReload();
		this.status_reload();
	}
});