var vueUserList = new Vue({
	el : "#user_manager_div",
	data : {
		users : [],
		pager : {}
	},
	methods : {
		
	},
	created: function () {
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
	    	}
	    });
    }
});