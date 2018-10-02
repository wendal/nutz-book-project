var vueDeviceList = new Vue({
	el : "#device_manager_div",
	data : {
		devices : [],
		pager : {pageNumber:1,pageCount:1,pageSize:10},
		query : {
			nickname : "",
			imei : "",
			iccid : "",
			deviceName : "",
			online : false
		}
	},
	methods : {
		dataReload : function() {
			var q = {
					pageSize : this.pager.pageSize,
					pageNumber : this.pager.pageNumber,
					nickname : this.query.nickname,
					imei : this.query.imei,
					iccid : this.query.iccid,
					deviceName : this.query.deviceName
			};
			if (this.query.online)
				q["online"] = true;
			$.ajax({
		    	url : base + "/aliyuniot/admin/query",
		    	dataType : "json",
		    	data : q,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueDeviceList.devices = re.data.list;
		    			vueDeviceList.pager = re.data.pager;
		    		} else if (re && re.msg) {
						layer.alert(re.msg);
					}
		    	},
		    	fail : function(err) {
		    		layer.alert("加载失败:" + err);
		    	},
		    	error : function (err){
		    		layer.alert("加载失败:" + err);
		    	}
		    });
		},
	    changePage: function(to_page) {
	    	this.pager.pageNumber = to_page;
	    	this.dataReload();
	    },
		add_device : function() {
			layer.prompt({
				  formType: 2,
				  value: '',
				  title: '请输入设备IMEI'
				},function(value, index, elem){
				  layer.close(index);
				  if (!value)
					  return;
				  $.ajax({
						url : base + "/aliyuniot/admin/add",
						type : "POST",
						data : {imeis:value},
						success : function(re) {
							if (re && re.ok) {
								layer.alert("添加成功");
								vueDeviceList.dataReload();
							}
							else {
								layer.alert("添加失败: " + re.msg);
							}
						},
				    	fail : function(err) {
				    		layer.alert("加载失败:" + err);
				    	},
				    	error : function (err){
				    		layer.alert("加载失败:" + err);
				    	}
					});
				});
		},
		sync_aliyun_device : function() {
			$.ajax({
				url : base + "/aliyuniot/admin/sync/aliyun",
				type : "POST",
				success : function(re) {
					if (re && re.ok) {
						layer.alert("刷新成功: " + re.msg);
						vueDeviceList.dataReload();
					}
					else {
						layer.alert("刷新失败: " + re.msg);
					}
				},
		    	fail : function(err) {
		    		layer.alert("刷新失败:" + err);
		    	},
		    	error : function (err){
		    		layer.alert("刷新失败:" + err);
		    	}
			});
		}
	},
	created: function () {
	    this.dataReload();
    }
});