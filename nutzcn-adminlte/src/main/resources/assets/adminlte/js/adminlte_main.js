
var mainSidebar = new Vue({
	el : "#main_sidebar",
	data : {
		menus : []
	},
	methods : {
		switch_content : function(path) {
			if (console)
				console.info(path);
			$("#main_content").load(path);
		}
	}
});