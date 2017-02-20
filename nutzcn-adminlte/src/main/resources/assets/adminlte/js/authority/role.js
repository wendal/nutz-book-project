var vueRoleList = new Vue({
	el : "#role_manager_div",
	data : {
		roles : [],
		pager : {pageNumber:1,pageCount:1}
	},
	methods : {
		dataReload : function() {
			$.ajax({
		    	url : base + "/admin/authority/roles",
		    	dataType : "json",
		    	data : "pageSize=10&pageNumber="+this.pager.pageNumber,
		    	success : function(re) {
		    		if (re && re.ok) {
		    			vueRoleList.roles = re.data.list;
		    			vueRoleList.pager = re.data.pager;
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
	    		  title: '请输入角色名称,只能是英文字母'
	    		}, function(value, index, elem){
	    		  layer.close(index);
	    		  if (value) {
	    			  $.ajax({
	    			    	url : base + "/admin/authority/role/add",
	    			    	dataType : "json",
	    			    	data : "name="+value,
	    			    	type : "POST",
	    			    	success : function(re) {
	    			    		if (console)
	    			    			console.info(re);
	    			    		if (re && re.ok) {
	    			    			vueRoleList.dataReload();
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
	    do_delete : function(role_id, role_name) {
	    	layer.prompt({
	    		  formType: 0,
	    		  value: '',
	    		  title: '请输入y以确认删除角色'+role_name
	    		}, function(value, index, elem){
	    		  layer.close(index);
	    		  if (value == "y") {
	    			  $.ajax({
	    			    	url : base + "/admin/authority/role/delete",
	    			    	dataType : "json",
	    			    	data : "id="+role_id,
	    			    	type : "POST",
	    			    	success : function(re) {
	    			    		if (console)
	    			    			console.info(re);
	    			    		if (re && re.ok) {
	    			    			vueRoleList.dataReload();
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
	    update_alias : function(role_id, role_alias) {
	    	layer.prompt({
	    		  formType: 0,
	    		  value: role_alias,
	    		  title: '请输入角色别名'
	    		}, function(value, index, elem){
	    		  layer.close(index);
	    		  if (value) {
	    			  $.ajax({
	    			    	url : base + "/admin/authority/role/update",
	    			    	dataType : "json",
	    			    	type : "POST",
	    			    	data : "{role:{id:"+role_id+",alias:'"+value+"'}}",
	    			    	type : "POST",
	    			    	contentType: "application/json; charset=utf-8",
	    			    	success : function(re) {
	    			    		if (re && re.ok) {
	    			    			vueRoleList.dataReload();
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