//function() {
var vueHotplugList = new Vue({
	el : "#hotplug_manager_div",
	data : {
		hotplugs : [],
		pager : {}
	},
	methods : {
		dataReload : function() {
			$.ajax({
				url : base + "/admin/hotplug/list",
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						vueHotplugList.hotplugs = re.data.list;
						vueHotplugList.pager = re.data.pager;
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
		do_enable : function(hotplug_sha1) {
			$.ajax({
				url : base + "/admin/hotplug/enable",
				type : "POST",
				data : "sha1="+hotplug_sha1,
				dataType : "json",
				success : function(re) {
					if (console)
						console.info(re);
					if (re && re.ok) {
						vueHotplugList.dataReload();
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
		do_disable : function(hotplug_name) {
			if ("core" == hotplug_name) {
				layer.alert("core模块不允许卸载");
			} else if ("adminlte" == hotplug_name) {
				layer.alert("adminlte模块不允许卸载");
			} else {
				$.ajax({
					url : base + "/admin/hotplug/disable",
					method : "POST",
					data : "name=" + hotplug_name,
					success : function() {
						vueHotplugList.dataReload();
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
		do_delete: function(hotplug_sha1) {
			$.ajax({
				url : base + "/admin/hotplug/delete",
				method : "POST",
				data : "sha1=" + hotplug_sha1,
				dataType : "json",
				success : function(re) {
					if (re && re.ok && re.data)
						vueHotplugList.dataReload();
					else if (re && re.msg) {
						layer.alert("删除失败: " + re.msg);
					} else if (re && !re.data) {
						layer.alert("删除失败: 文件被锁定??");
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
var uploader = WebUploader.create({

	// swf文件路径
	// swf: BASE_URL + '/js/Uploader.swf',

	// 文件接收服务端。
	server : base + '/admin/hotplug/add',

	// 选择文件的按钮。可选。
	// 内部根据当前运行是创建，可能是input元素，也可能是flash.
	pick : '#hotplug_picker',
	auto : true,

	// 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
	resize : false
});
uploader.on( 'uploadSuccess', function( file ) {
    layer.alert("上传成功: " + file.name);
    vueHotplugList.dataReload();
});

uploader.on( 'uploadError', function( file ) {
	layer.alert("上传出错: " + file.name);
});

uploader.on( 'uploadComplete', function( file ) {
});
// }();
