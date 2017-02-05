var vueRoleList = new Vue({
	el : "#role_manager_div",
	data : {
		roles : [],
		pager : {}
	},
	methods : {
		dataReload : function() {
			$.ajax({
		    	url : base + "/admin/authority/roles",
		    	dataType : "json",
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueRoleList.roles = re.data.list;
		    			vueRoleList.pager = re.data.pager;
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