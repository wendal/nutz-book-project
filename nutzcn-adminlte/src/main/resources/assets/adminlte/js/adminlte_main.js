
var mainSidebar = new Vue({
	el : "#main_sidebar",
	data : {
		menus : []
	},
	methods : {
		switch_page : function(path) {
			if (!path.startsWith("/"))
				path = "/" + path;
			path = base + "/adminlte/page" + path
			if (console)
				console.info(path);
			$("#main_content").load(path);
		}
	},
	created : function() {
		$.ajax({
			url : base + "/admin/hotplug/list?active=true",
			dataType : "json",
			success : function(re) {
				if (re && re.ok) {
					var menus = [];
					for (var i in re.data.list) {
						var hc = re.data.list[i];
						if (hc.menu) {
							if (console)
								console.log(hc.name, hc.menu)
							menus.push(hc.menu);
						}
					}
					mainSidebar.menus = menus;
				} else if (re && re.message) {
					layer.alert(re.message);
				}
			}
		});
	}
});