
var mainSidebar = new Vue({
	el : "#main_sidebar",
	data : {
		menus : []
	},
	methods : {
		switch_page : function(path) {
			path = base + "/adminlte/page" + path
			if (console)
				console.info(path);
			$("#main_content").load(path);
		}
	}
});