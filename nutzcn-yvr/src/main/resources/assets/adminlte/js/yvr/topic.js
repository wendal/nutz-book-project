var vueTopicList = new Vue({
	el : "#topic_manager_div",
	data : {
		topics : [],
		pager : {pageNumber:1,pageCount:1}
	},
	methods : {
		dataReload : function() {
			$.ajax({
		    	url : base + "/yvr/admin/query",
		    	dataType : "json",
		    	data : "pageSize=10&pageNumber="+this.pager.pageNumber,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueTopicList.topics = re.data.list;
		    			vueTopicList.pager = re.data.pager;
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
		},
	    changePage: function(to_page) {
	    	this.pager.pageNumber = to_page;
	    	this.dataReload();
	    },
	    update_title : function (topic_id, topic_title) {
	    	layer.prompt({title: '请输入新标题,然后确认', formType: 0, value: topic_title}, function(new_title, index){
	    		  layer.close(index);
	    		  if (new_title) {
	    			  $.ajax({
	  					url : base + "/yvr/admin/update",
	  					type : "post",
	  			    	dataType : "json",
	  			    	data : "opt=title&id="+topic_id+"&title="+new_title,
	  			    	success : function(re) {
	  			    		if (re && re.ok) {
	  			    			layer.alert("更新成功");
	  			    			vueTopicList.dataReload();
	  			    		} else if (re && re.msg) {
	  							layer.alert(re.msg);
	  						}
	  			    	}
	  				});
	    		  }
	    		});
		},
	    update_type : function (topic_id) {
	    	layer.prompt({title: '请输入新类型,然后确认', formType: 0}, function(new_type, index){
	    		  layer.close(index);
	    		  if (new_type) {
	    			  $.ajax({
	  					url : base + "/yvr/admin/update",
	  					type : "post",
	  			    	dataType : "json",
	  			    	data : "opt=type&id="+topic_id+"&type="+new_type,
	  			    	success : function(re) {
	  			    		if (re && re.ok) {
	  			    			layer.alert("更新成功");
	  			    			vueTopicList.dataReload();
	  			    		} else if (re && re.msg) {
	  							layer.alert(re.msg);
	  						}
	  			    	}
	  				});
	    		  }
	    	});
		},
		update_good : function (topic_id, is_good) {
	    	$.ajax({
	  			url : base + "/yvr/admin/update",
	  			type : "post",
	  			dataType : "json",
	  			data : "opt=good&id="+topic_id+"&good="+is_good,
	  			success : function(re) {
	  			    if (re && re.ok) {
	  			    	layer.alert("更新成功");
	  			    	vueTopicList.dataReload();
	  			    } else if (re && re.msg) {
	  					layer.alert(re.msg);
	  				}
	  			 }
	    	});
		},
		update_top : function (topic_id, is_top) {
	    	$.ajax({
	  			url : base + "/yvr/admin/update",
	  			type : "post",
	  			dataType : "json",
	  			data : "opt=top&id="+topic_id+"&good="+is_top,
	  			success : function(re) {
	  			    if (re && re.ok) {
	  			    	layer.alert("更新成功");
	  			    	vueTopicList.dataReload();
	  			    } else if (re && re.msg) {
	  					layer.alert(re.msg);
	  				}
	  			 }
	    	});
		},
	    do_delete: function(topic_id) {
	    	layer.prompt({title: '请输入y确认', formType: 0}, function(do_confim, index){
	    		  layer.close(index);
	    		  if (do_confim == 'y') {
	    			  $.ajax({
	  					url : base + "/yvr/admin/topic/delete",
	  					type : "post",
	  			    	dataType : "json",
	  			    	data : "id="+topic_id,
	  			    	success : function(re) {
	  			    		if (re && re.ok) {
	  			    			layer.alert("删除成功");
	  			    			vueTopicList.dataReload();
	  			    		} else if (re && re.msg) {
	  							layer.alert(re.msg);
	  						}
	  			    	}
	  				});
	    		  }
	    	});
		},
		get_topic_uri : function(topic_id) {
			return base + "/yvr/t/" + topic_id;
		}
	},
	created: function () {
	    this.dataReload();
    }
});