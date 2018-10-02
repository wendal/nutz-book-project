var nutzdw = new Vue({
	el : '#nutzdw_list',
	data : {
		list : [],
		_match : ""
	},
	methods : {
		doReload : function() {
			this.$http.get(base + "/nutzdw/list", {}).then(
					function(resp) {
						// console.log(resp.ok);
						if (resp.ok) {
							var re = resp.json();
							//console.info(re.data);
							for (var index in re.data) {
								var tmp = re.data[index].name;
								if (tmp.indexOf("/") > 0)
									re.data[index].name = tmp.substring(tmp.lastIndexOf("/") + 1);
								re.data[index].url = dw_domain + tmp;
							}
							this.list = re.data;
						}
					});
		}
	},
	created : function() {
		this.doReload();
	}
});