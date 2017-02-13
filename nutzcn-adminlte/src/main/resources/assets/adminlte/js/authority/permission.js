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
	    }
	},
	created: function () {
	    this.dataReload();
    }
});