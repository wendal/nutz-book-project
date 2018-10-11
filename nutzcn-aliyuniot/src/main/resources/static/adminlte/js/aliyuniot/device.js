// 对Date的扩展，将 Date 转化为指定格式的String   
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
// 例子：   
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18   
Date.prototype.Format = function(fmt)   
{ //author: meizz   
  var o = {   
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "h+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
};
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
			online : true
		},
		ui : {
			show_deviceSecret : false,
			auto_reload : false
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
		    		//if (console)
		    		//	console.info(re);
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
				  title: '请输入设备名称,通常是IMEI,可以用输入多个,用英文逗号或换行分隔'
				},function(value, index, elem){
				  layer.close(index);
				  if (!value)
					  return;
				  $.ajax({
						url : base + "/aliyuniot/admin/add",
						type : "POST",
						data : {deviceNames:value},
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
		},
		publish_msg : function(device_id) {
			layer.prompt({
				  formType: 2,
				  value: '{}',
				  title: '请输入消息内容'
				},function(value, index, elem){
				  layer.close(index);
				  if (!value)
					  return;
				  $.ajax({
						url : base + "/aliyuniot/admin/publish/mqtt",
						type : "POST",
						data : {ids:device_id,cnt:value},
						success : function(re) {
							if (re && re.ok) {
								if (re.data[device_id] && re.data[device_id].success) {
									layer.alert("发送成功");
								}
								else {
									if (re.data[device_id]) {
										layer.alert("发送失败: " + re.data[device_id].errorMessage)
									}
									else {
										layer.alert("发送失败:  设备不存在?");
									}
								}
							}
							else {
								layer.alert("发送失败: " + re.msg);
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
		show_stat_log : function(device_id) {
			$.ajax({
				url : base + "/aliyuniot/admin/statlog/query",
				type : "POST",
				data : {deviceId:device_id},
				success : function(re) {
					if (re && re.ok) {
						var h = "<table class=\"table table-bordered table-hover\"><thead>" +
									"<tr>" +
									   "<th>时间</th>"	+
									   "<th>状态</th>"	+
									"</tr>" +
								"</thead>";
						h += "<tbody>"
						for (var i=0;i<re.data.list.length;i++) {
							h += "<tr>"
							var d = re.data.list[i];
							h+= "<th>" + (new Date(d.time)).Format("yyyy-MM-dd hh:mm:ss.S") + "</th>"
							h+= "<th>" + (d.online ? "上线" : "离线") + "</th>"
							h += "</tr>"
						}
						h += "</tbody>";
						h += "</table>";
						if (console)
							console.log(h);
						layer.open({
							  type: 1, 
							  content: h,
							  area: '640px',
							  shadeClose : true,
							  title : "设备在线离线日志"
					    });
					}
					else {
						layer.alert("发送失败: " + re.msg);
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
		show_msg_log : function(device_id) {
			$.ajax({
				url : base + "/aliyuniot/admin/mqttmsg/query",
				type : "POST",
				data : {deviceId:device_id},
				success : function(re) {
					if (re && re.ok) {
						var h = "<table class=\"table table-bordered table-hover\"><thead>" +
									"<tr>" +
									   "<th>时间</th>"	+
									   "<th>Topic</th>"	+
									   "<th>方向</th>"	+
									   "<th>内容</th>"	+
									"</tr>" +
								"</thead>";
						h += "<tbody>"
						for (var i=0;i<re.data.list.length;i++) {
							h += "<tr>"
							var d = re.data.list[i];
							h+= "<th>" + (new Date(d.time)).Format("yyyy-MM-dd hh:mm:ss.S") + "</th>"
							h+= "<th>" + (d.topic) + "</th>"
							h+= "<th>" + (d.dir ? "下行" : "上行") + "</th>"
							h+= "<th>" + window.atob(d.cnt) + "</th>"
							h += "</tr>"
						}
						h += "</tbody>";
						h += "</table>";
						if (console)
							console.log(h);
						layer.open({
							  type: 1, 
							  content: h,
							  area: '1024px',
							  shadeClose : true,
							  title : "设备上下行信息"
					    });
					}
					else {
						layer.alert("发送失败: " + re.msg);
					}
				},
		    	fail : function(err) {
		    		layer.alert("加载失败:" + err);
		    	},
		    	error : function (err){
		    		layer.alert("加载失败:" + err);
		    	}
			});
		}
	},
	created: function () {
	    this.dataReload();
    }
});
setInterval(function() {
	if (vueDeviceList.ui.auto_reload) {
		//console.log("vueDeviceList.ui.auto_reload=" + vueDeviceList.ui.auto_reload);
		vueDeviceList.dataReload();
	}
}, 3000);