var bcListVue = new Vue({
	el: '#bclist',
	data: {
	    sayhi: '打赏主页',
	    pageSize : 20,
	    pageNumber : 1,
	    pageCount : 1,
	    //recordCount : 0,
	    list : [],
	    base : base,
	    fromUser : 0,
	    toUser : 0
	},
	methods : {
		doReload() {
			this.$http.get(base+"/pay/bc/query", {params:{pageNumber:this.pageNumber, pageSize:this.pageSize}}).then(function(resp){
				console.log(resp.ok);
				if (resp.ok) {
					var re = resp.json();
					console.info(re);
					this.pageSize = re.pager.pageSize;
					this.pageCount = re.pager.pageCount;
					this.pageNumber = re.pager.pageNumber;
					this.list = re.list;
				}
			});
		},
		do_pay(uid) {
			pay_tips(uid);
		}
	},
	created: function () {
		this.doReload();
	}
});