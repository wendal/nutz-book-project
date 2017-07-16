var vuePermisssionList = new Vue({
	el : "#permission_manager_div",
	data : {
		permissions : [],
		pager : {pageNumber:1,pageCount:1}
	},
	methods : {
		dataReload : function () {
			$.ajax({
		    	url : base + "/admin/authority/permissions",
		    	dataType : "json",
		    	data : "pageSize=10&pageNumber="+this.pager.pageNumber,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vuePermisssionList.permissions = re.data.list;
		    			vuePermisssionList.pager = re.data.pager;
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
	    do_add : function() {
	    	layer.prompt({
	    		  formType: 0,
	    		  value: '',
	    		  title: '请输入	权限名称'
	    		}, function(value, index, elem){
	    		  layer.close(index);
	    		  if (value) {
	    			  $.ajax({
	    			    	url : base + "/admin/authority/permission/add",
	    			    	dataType : "json",
	    			    	data : {"name":value},
	    			    	type : "POST",
	    			    	success : function(re) {
	    			    		if (console)
	    			    			console.info(re);
	    			    		if (re && re.ok) {
	    			    			vuePermisssionList.dataReload();
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
	    		  }
	    		});
	    },
	    do_delete : function(permission_id, permission_name) {
	    	layer.prompt({
	    		  formType: 0,
	    		  value: '',
	    		  title: '请输入y以确认删除权限('+role_name + ")"
	    		}, function(value, index, elem){
	    		  layer.close(index);
	    		  if (value == "y") {
	    			  $.ajax({
	    			    	url : base + "/admin/authority/permission/delete",
	    			    	dataType : "json",
	    			    	data : "id="+permission_id,
	    			    	type : "POST",
	    			    	success : function(re) {
	    			    		if (console)
	    			    			console.info(re);
	    			    		if (re && re.ok) {
	    			    			vuePermisssionList.dataReload();
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
	    		  }
	    		});
	    },
	    update_alias : function(permission_id, permission_alias) {
	    	layer.prompt({
	    		  formType: 0,
	    		  value: permission_alias,
	    		  title: '请输入新的权限别名'
	    		}, function(value, index, elem){
	    		  layer.close(index);
	    		  if (value) {
	    			  $.ajax({
	    			    	url : base + "/admin/authority/permission/update",
	    			    	dataType : "json",
	    			    	type : "POST",
	    			    	data : "{permission:{id:"+permission_id+",alias:'"+value+"'}}",
	    			    	type : "POST",
	    			    	contentType: "application/json; charset=utf-8",
	    			    	success : function(re) {
	    			    		if (re && re.ok) {
	    			    			vuePermisssionList.dataReload();
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
	    		  }
	    		});
	    }
	},
	created: function () {
	    this.dataReload();
    }
});