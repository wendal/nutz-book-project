
var config_map = {
		"website.urlbase" : "网站地址",
		"website.title" : "网站名称",
		"website.shortname" : "网站简称",
		"website.description" : "网站简介",
		"website.author" : "网站作者",
		"website.keywords" : "默认关键字",
		"website.long_description" : "网站完整描述",
		"website.beian" : "备案号",
		"website.ltd" : "公司名称",
		"website.homepage" : "默认首页",
		"website.loginpage" : "默认登录页"
};
var vueBasicConfigList = new Vue({
	el : "#system_basic_div",
	data : {
		configs : []
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/config/list?prefix=website",
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
		},
		do_save : function() {
			var p = {};
			for (var i in this.configs) {
				p[this.configs[i].name] = this.configs[i].value;
				if ("website.homepage" == this.configs[i].name) {
					if (this.configs[i].value == "/") {
						layer.alert("默认首页不可以是/. 通常是某个模块的根路径,例如管理后台/adminlte");
						return;
					}
				}
				if ("website.loginpage" == this.configs[i].name) {
					if (this.configs[i].value == "/user/login") {
						layer.alert("默认登录页不可以是/user/login. 默认/adminlte/user/login");
						return;
					}
				}
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
		}
	},
	created : function() {
		this.dataReload();
	}
});