var vueHotplugRepoList = new Vue({
	el : "#hotplug_repo_div",
	data : {
		repos : [
			{
				id:"1",
				name : "local",
				alias : "本地库",
				enable : true
			}
		],
		pager : {}
	},
	methods : {
		dataReload : function() {
			$.ajax({
		    	url : base + "/admin/hotplug/repos",
		    	dataType : "json",
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueHotplugRepoList.repos = re.data.list;
		    			vueHotplugRepoList.pager = re.data.pager;
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
	created: function () {
	    //this.dataReload();
    }
});