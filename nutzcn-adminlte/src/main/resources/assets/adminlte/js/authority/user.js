var vueUserList = new Vue({
	el : "#user_manager_div",
	data : {
		users : [],
		pager : {pageNumber:1,pageCount:1}
	},
	methods : {
		dataReload : function() {
			$.ajax({
		    	url : base + "/admin/authority/users",
		    	dataType : "json",
		    	data : "pageSize=10&pageNumber="+this.pager.pageNumber,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueUserList.users = re.data.list;
		    			vueUserList.pager = re.data.pager;
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
	    user_change_password : function (uid) {
	    	var password = prompt("输入新密码");
	    	if (password) {
				$.ajax({
					url : base + "/admin/authority/user/update/password",
					type : "post",
			    	dataType : "json",
			    	data : "password="+password+"&id="+uid,
			    	success : function(re) {
			    		if (console)
			    			console.info(re);
			    		if (re && re.ok) {
			    			layer.alert("更新成功");
			    			vueUserList.dataReload();
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
		},
		user_setLock : function(locked) {
			layer.alert("暂未实现");
		},
	    userAdd: function() {
			var username = prompt("输入用户名");
			if (username) {
				$.ajax({
					url : base + "/admin/authority/user/add",
					type : "post",
			    	dataType : "json",
			    	data : "password=123456&name="+username,
			    	success : function(re) {
			    		if (console)
			    			console.info(re);
			    		if (re && re.ok) {
			    			layer.alert("添加成功");
			    			vueUserList.dataReload();
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
		}
	},
	created: function () {
	    this.dataReload();
    }
});