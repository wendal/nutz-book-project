var vueNgrokTokensList = new Vue({
	el : "#ngrok_tokens_div",
	data : {
		tokens : [],
		pager : {pageNumber:1,pageCount:1}
	},
	methods : {
		dataReload : function () {
			$.ajax({
		    	url : base + "/admin/ngrok/tokens",
		    	dataType : "json",
		    	data : "pageSize=10&pageNumber="+this.pager.pageNumber,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueNgrokTokensList.tokens = re.data.list;
		    			vueNgrokTokensList.pager = re.data.pager;
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
	    do_delete: function(token_token) {
	    	$.ajax({
		    	url : base + "/admin/ngrok/token/delete",
		    	dataType : "json",
		    	type : "POST",
		    	data : "&token="+token_token,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			layer.alert("删除成功");
		    			vueNgrokTokensList.dataReload();
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
		clear_users : function(day) {
			$.ajax({
				url : base + "/admin/ngrok/token/clear",
				type : "POST",
				data : "day=" + day,
				dataType : "json",
				success : function(re) {
					if (re && re.ok) {
						vueNgrokClient.client_status = re.data;
					} else if (re && re.msg) {
						layer.alert("有问题? " + re.msg);
					}
				},
			});
		}
	},
	created: function () {
	    this.dataReload();
    }
});