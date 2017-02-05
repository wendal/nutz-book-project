var vueUserList = new Vue({
	el : "#user_manager_div",
	data : {
		users : [],
		pager : {}
	},
	methods : {
		dataReload : function() {
			$.ajax({
		    	url : base + "/admin/authority/users",
		    	dataType : "json",
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueUserList.users = re.data.list;
		    			vueUserList.pager = re.data.pager;
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