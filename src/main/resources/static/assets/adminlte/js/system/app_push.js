

var vueAppPushConfigList = new Vue({
	el : "#system_app_push_div",
	data : {
		jpush : {masterSecret:"", enable:false, appKey:""},
		xmpush : {appSecret:"",enable:false}
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/config/list?prefix=jpush",
				dataType : "json",
				success : function(re) {
					if (re && re.ok) {
						vueAppPushConfigList.jpush.enable = "true" == re.data["jpush.enable"];
						vueAppPushConfigList.jpush.masterSecret = re.data["jpush.masterSecret"];
						vueAppPushConfigList.jpush.appKey = re.data["jpush.appKey"];
					} else if (re && re.msg) {
						layer.alert(re.msg);
					}
				}
			});
			$.ajax({
				url : base + "/admin/config/list?prefix=xmpush",
				dataType : "json",
				success : function(re) {
					if (re && re.ok) {
						vueAppPushConfigList.xmpush.enable = "true" == re.data["xmpush.enable"];
						vueAppPushConfigList.xmpush.appSecret = re.data["xmpush.appSecret"];
					} else if (re && re.msg) {
						layer.alert(re.msg);
					}
				}
			});
		},
		do_save_xmpush : function() {
			var p = {};
			//for (var i in this.configs) {
				p["xmpush.enable"] = this.xmpush.enable;
				p["xmpush.appSecret"] = this.xmpush.appSecret;
			///}
			$.ajax({
				url : base + "/admin/config/save?notify=appPushService",
				type : "POST",
				data : p,
				dataType : "json",
				success : function(re) {
					if (re && re.ok) {
						vueAppPushConfigList.dataReload();
						layer.alert("保存完成");
					} else if (re && re.msg) {
						layer.alert("失败:" + re.msg);
					}
				}
			});
		},
		do_save_jpush : function() {
			var p = {};
			//for (var i in this.configs) {
				p["jpush.enable"] = this.jpush.enable;
				p["jpush.masterSecret"] = this.jpush.masterSecret;
				p["jpush.appKey"] = this.jpush.appKey;
			//}
			$.ajax({
				url : base + "/admin/config/save",
				type : "POST",
				data : p,
				dataType : "json",
				success : function(re) {
					if (re && re.ok) {
						vueAppPushConfigList.dataReload();
						layer.alert("保存完成");
					} else if (re && re.msg) {
						layer.alert("失败:" + re.msg);
					}
				}
			});
		}
	},
	created : function() {
		this.dataReload();
	}
});