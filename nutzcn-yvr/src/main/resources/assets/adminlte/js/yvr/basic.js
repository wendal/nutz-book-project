
var config_map = {
		"yvr.title" : "论坛名称",
		"yvr.logo_path" : "论坛logo路径",
		//"yvr.shortname" : "网站简称",
		//"yvr.description" : "网站简介",
		//"yvr.author" : "网站作者",
		"yvr.keywords" : "默认关键字",
		//"yvr.long_description" : "网站完整描述",
		"yvr.tips_of_add" : "发帖提示语",
		"yvr.allow_image_upload" : "是否允许上传图片"
};
var vueBasicConfigList = new Vue({
	el : "#yvr_config_div",
	data : {
		configs : []
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/config/list?prefix=yvr",
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