var vueSlogList = new Vue({
	el : "#slog_list_div",
	data : {
		slog_list : [],
		pager : {pageNumber:1,pageCount:1}
	},
	methods : {
		dataReload : function () {
			$.ajax({
		    	url : base + "/admin/slog/list",
		    	dataType : "json",
		    	data : "pageSize=10&pageNumber="+this.pager.pageNumber,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueSlogList.slog_list = re.data.list;
		    			vueSlogList.pager = re.data.pager;
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
	    }
	},
	created: function () {
	    this.dataReload();
    }
});