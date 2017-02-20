var bcListVue = new Vue({
	el: '#bclist',
	data: {
	    sayhi: '打赏主页',
	    pageSize : 10,
	    pageNumber : 1,
	    pageCount : 1,
	    //recordCount : 0,
	    list : [],
	    base : base,
	    fromUser : 0,
	    toUser : 0,
	    amount : 0,
	    list2 : [
	             ["2016-01-07","淡蓝","香港vps(高配) 1年"],
	             ["2016-01-08","张乐","1元"],
	             ["2016-01-08","刘方果","6元"],
	             ["2016-01-08","陈全来","5.21元"],
	             ["2016-01-22","王江伟","9.9元"],
	             ["2016-06-05","孙海","6.66元"],
	             ["2016-07-22","哦","22.22元"],
	             ["2016-07-25","杨建","6.66元"],
	             ["2016-07-28","木矛","19.99元"],
	             ["2016-08-01","木矛","79.99元"],
	             ["2016-08-01","宗美","3.00元"],
	             ["2016-08-04","志华","66.00元"],
	             ["2016-08-04","A.lawliet","500.00元"],
	             ]
	},
	methods : {
		doReload : function() {
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
		do_pay: function(uid) {
			pay_tips(uid, this.amount);
		},
		jump: function(to) {
			this.pageNumber = to;
			this.doReload();
		}
	},
	created: function () {
		this.doReload();
	}
});