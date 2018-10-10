
var config_map = {
		"aliyuniot.name" : "项目名称",
		"aliyuniot.uid" : "UID",
		"aliyuniot.regionId" : "RegionId",
		"aliyuniot.accessKey" : "AccessKey",
		"aliyuniot.accessSecret" : "AccessSecret",
		"aliyuniot.productKey" : "ProductKey",
		"aliyuniot.productSecret" : "ProductSecret",
		//"aliyuniot.http2Enable" : "是否使用http2监听",
};
var vueBasicConfigList = new Vue({
	el : "#aliyuniot_config_div",
	data : {
		configs : [],
		http2stat : false
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/config/list?prefix=aliyuniot",
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
						vueBasicConfigList.configs = configs
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
			$.ajax()
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
						vueBasicConfigList.dataReload();
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
		get_http2stat : function(auto_check) {
			$.ajax({
				url : base + "/aliyuniot/admin/listen/status",
				dataType : "json",
				success : function(re) {
					if (re) {
						vueBasicConfigList.http2stat = true;
					}
					else {
						vueBasicConfigList.http2stat = false;
					}
				},
				fail : function(err) {
					if (!auto_check)
						layer.alert("加载失败:" + err);
				},
				error : function(err) {
					if (!auto_check)
						layer.alert("加载失败:" + err);
				}
			});
		},
		start_http2 : function(auto_check) {
			$.ajax({
				url : base + "/aliyuniot/admin/listen/start",
				dataType : "json",
				success : function() {
					layer.alert("启动中...");
				},
				fail : function(err) {
					if (!auto_check)
						layer.alert("失败了" + err);
				},
				error : function(err) {
					if (!auto_check)
						layer.alert("加载失败:" + err);
				}
			});
		},
		stop_http2 : function(auto_check) {
			$.ajax({
				url : base + "/aliyuniot/admin/listen/stop",
				dataType : "json",
				success : function() {
					layer.alert("关闭中...");
				},
				fail : function(err) {
					if (!auto_check)
						layer.alert("加载失败:" + err);
				},
				error : function(err) {
					if (!auto_check)
						layer.alert("加载失败:" + err);
				}
			});
		}
	},
	created : function() {
		this.dataReload();
		this.get_http2stat();
	}
});
setInterval(function() {
	vueBasicConfigList.get_http2stat(true);
}, 3000);