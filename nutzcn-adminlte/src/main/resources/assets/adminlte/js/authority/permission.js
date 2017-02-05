var vuePermisssionList = new Vue({
	el : "#permission_manager_div",
	data : {
		permissions : [],
		pager : {}
	},
	methods : {
		dataReload : function () {
			$.ajax({
		    	url : base + "/admin/authority/permissions",
		    	dataType : "json",
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vuePermisssionList.permissions = re.data.list;
		    			vuePermisssionList.pager = re.data.pager;
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