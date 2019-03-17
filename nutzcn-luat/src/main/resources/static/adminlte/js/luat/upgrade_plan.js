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
var _app = new Vue({
	el : "#plan_manager_div",
	data : {
		plans : [],
		projects : [],
		packages : [],
		pager : {pageNumber:1,pageCount:1,pageSize:10},
		query : {
			nickname : "",
			imei : "",
			iccid : "",
			projectId : ""
		},
		ui : {
			mode : "list",
		},
		cur_plan : {}
	},
	methods : {
		dataReload : function() {
			var q = {
					pageSize : this.pager.pageSize,
					pageNumber : this.pager.pageNumber,
					imei : this.query.imei,
					iccid : this.query.iccid
			}
			$.ajax({
		    	url : base + "/luat/admin/upgrade/plan/query",
		    	dataType : "json",
		    	data : q,
		    	success : function(re) {
		    		//if (console)
		    		//	console.info(re);
		    		if (re && re.ok) {
		    			_app.plans = re.data.list;
		    			_app.pager = re.data.pager;
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
		add_plan : function() {
			this.ui.mode = "add";
			this.cur_plan = {};
		},
		show_plan : function(plan) {
			this.ui.mode = "update";
			this.cur_plan = plan
		},
		do_enable : function(plan_id, en) {
			$.ajax({
		    	url : base + "/luat/admin/upgrade/plan/enable",
		    	dataType : "json",
		    	data : {id:plan_id, enable:en},
		    	type : "POST",
		    	success : function(re) {
		    		if (re && re.ok) {
		    			if (en) {
		    				layer.alert("激活成功", {shadeClose:true});
		    			}
		    			else{
		    				layer.alert("禁用成功", {shadeClose:true});
		    			}
		    			_app.dataReload();
		    		}
		    		else if (re && re.msg)
		    			layer.alert("修改失败:" + re.msg, {shadeClose:true});
		    	},
		    	fail : function(re) {
		    		layer.alert("修改失败", {shadeClose:true});
		    	}
		    });
		},
		add_plan_action: function() {
			var plan = JSON.parse(JSON.stringify(_app.cur_plan))
			plan.pkg = null;
			if (!this.cur_plan.pkgId) {
				// 没有可用的固件包?
				layer.alert("请先选择固件包,如果没有请上传",{shadeClose:true});
				return;
			}
			$.ajax({
		    	url : base + "/luat/admin/upgrade/plan/add",
		    	dataType : "json",
		    	data : JSON.stringify(plan),
		    	type : "POST",
		    	contentType:"text/json",
		    	success : function(re) {
		    		if (re && re.ok)
		    			layer.alert("添加完成", {shadeClose:true});
		    		else if (re && re.msg)
		    			layer.alert("添加失败:" + re.msg, {shadeClose:true});
		    	},
		    	fail : function(re) {
		    		layer.alert("添加失败", {shadeClose:true});
		    	}
		    });
		},
		update_plan_action: function() {
			var plan = JSON.parse(JSON.stringify(_app.cur_plan))
			plan.pkg = null;
			$.ajax({
		    	url : base + "/luat/admin/upgrade/plan/update",
		    	dataType : "json",
		    	data : JSON.stringify(plan),
		    	type : "POST",
		    	contentType:"text/json",
		    	success : function(re) {
		    		if (re && re.ok)
		    			layer.alert("更新完成", {shadeClose:true});
		    		else if (re && re.msg)
		    			layer.alert("更新失败:" + re.msg, {shadeClose:true});
		    	},
		    	fail : function(re) {
		    		layer.alert("更新失败", {shadeClose:true});
		    	}
		    });
		},
		select_pkg : function() {
			this.cur_plan.pkgId = $("#pkg_select").find(":selected").val();
			console.log(this.cur_plan.pkgId);
		},
		//----------------------------
		// 选项目
		load_project_list: function() {
			$.ajax({
		    	url : base + "/luat/admin/project/query",
		    	dataType : "json",
		    	success : function(re) {
		    		if (re && re.ok) {
		    			_app.projects = re.data.list;
		    			_app.pager = re.data.pager;
		    		}
		    	}
		    });
		},
		change_project : function() {
			console.log($("#project_select").find(":selected").val())
			$.ajax({
		    	url : base + "/luat/admin/project/select",
		    	dataType : "json",
		    	type : "POST",
		    	data : {id: $("#project_select").find(":selected").val()},
		    	success : function(re) {
		    		_app.dataReload();
		    	}
		    });
		},
		get_current_project: function() {
			$.ajax({
		    	url : base + "/luat/admin/project/current",
		    	dataType : "json",
		    	success : function(re) {
		    		console.log(re)
		    		if (re && re.ok && re.data) {
		    			_app.query.projectId = re.data.id;
		    		}
		    		_app.load_project_list();
		    	}
		    });
		},
		reload_pkgs : function() {
			$.ajax({
		    	url : base + "/luat/admin/upgrade/package/query",
		    	dataType : "json",
		    	data : {pageSize:20},
		    	success : function(re) {
		    		if (re && re.ok) {
		    			_app.packages = re.data.list;
		    			_app.pager = re.data.pager;
		    		}
		    	}
		    });
		}
		//----------------------------------
	},
	created: function () {
	    this.dataReload();
	    this.get_current_project();
	    this.reload_pkgs();
    }
});