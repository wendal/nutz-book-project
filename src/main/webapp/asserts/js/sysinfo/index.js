var sysinfoListVue = new Vue({
	el: '#sysinfolist',
	data: {
		imaps : [],
	    base : base,
	    _match : ""
	},
	methods : {
		doReload : function() {
			this.$http.get(base+"/sysinfo/query", {params:{match:this._match}}).then(function(resp){
				console.log(resp.ok);
				if (resp.ok) {
					var re = resp.json();
					console.info(re);
					var tmp = [];
					for (var key in re) {
						tmp.push([key, re[key]]);
					}
					this.imaps = tmp;
				}
			});
		}
	},
	created: function () {
		this.doReload();
	}
});