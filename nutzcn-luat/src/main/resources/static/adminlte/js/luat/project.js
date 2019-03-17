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
var vueProjectList = new Vue({
	el : "#project_manager_div",
	data : {
		projects : [],
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
					nickname : this.query.nickname
			}
			$.ajax({
		    	url : base + "/luat/admin/project/query",
		    	dataType : "json",
		    	data : q,
		    	success : function(re) {
		    		//if (console)
		    		//	console.info(re);
		    		if (re && re.ok) {
		    			vueProjectList.projects = re.data.list;
		    			vueProjectList.pager = re.data.pager;
		    		} else if (re && re.msg) {
						layer.alert(re.msg);
					}
		    	},
		    	fail : function(err) {
		    		layer.alert("加载失败:" + err, {shadeClose:true});
		    	},
		    	error : function (err){
		    		layer.alert("加载失败:" + err, {shadeClose:true});
		    	}
		    });
		},
	    changePage: function(to_page) {
	    	this.pager.pageNumber = to_page;
	    	this.dataReload();
	    },
		add_project : function() {
			layer.prompt({
				  formType: 2,
				  value: '',
				  title: '请输入项目名称'
				},function(value, index, elem){
				  layer.close(index);
				  if (!value)
					  return;
				  $.ajax({
						url : base + "/luat/admin/project/add",
						type : "POST",
						data : {nickname:value},
						success : function(re) {
							if (re && re.ok) {
								layer.alert("添加成功", {shadeClose:true});
								vueProjectList.dataReload();
							}
							else {
								layer.alert("添加失败: " + re.msg, {shadeClose:true});
							}
						},
				    	fail : function(err) {
				    		layer.alert("加载失败:" + err, {shadeClose:true});
				    	},
				    	error : function (err){
				    		layer.alert("加载失败:" + err, {shadeClose:true});
				    	}
					});
				});
		}
	},
	created: function () {
	    this.dataReload();
    }
});