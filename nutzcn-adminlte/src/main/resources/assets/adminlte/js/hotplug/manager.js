var vueHotplugList = new Vue({
	el : "#hotplug_manager_div",
	data : {
		hotplugs : [],
		pager : {}
	},
	methods : {
		dataReload : function () {
			$.ajax({
		    	url : base + "/admin/hotplug/list",
		    	dataType : "json",
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueHotplugList.hotplugs = re.data.list;
		    			vueHotplugList.pager = re.data.pager;
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