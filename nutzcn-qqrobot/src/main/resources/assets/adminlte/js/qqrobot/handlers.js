var vueHandlerList = new Vue({
	el : "#qqrobot_handlers_div",
	data : {
		handlers : [],
		pager : {pageNumber:1,pageCount:1},
		cur_handler : {id:0},
	},
	methods : {
		dataReload : function() {
			$.ajax({
		    	url : base + "/qqrobot/admin/query",
		    	dataType : "json",
		    	data : "pageSize=10&pageNumber="+this.pager.pageNumber,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueHandlerList.handlers = re.data.list;
		    			vueHandlerList.pager = re.data.pager;
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
	    update_enable : function (handler_id, val) {
	    			  $.ajax({
	  					url : base + "/qqrobot/admin/update",
	  					type : "post",
	  			    	dataType : "json",
	  			    	data : {id:handler_id, enable:val},
	  			    	success : function(re) {
	  			    		if (re && re.ok) {
	  			    			layer.alert("设置成功");
	  			    			vueHandlerList.dataReload();
	  			    		} else if (re && re.msg) {
	  							layer.alert(re.msg);
	  						}
	  			    	}
	  				});
		},
	    show_detail : function (handler) {
	    	this.cur_handler = handler;
			layer.open({
				  type: 1,
				  shadeClose: true,
				  area: ['1000px', '600px'],
				  title: "详情",
				  border: [1],
				  content: $('#handler_detail'),
				  btn: ["更新"],
                  yes: function (index) {
                	  $.ajax({
                  		  url : base + "/qqrobot/admin/update",
    	  					  type : "post",
    	  			    	  dataType : "json",
    	  			    	  data : handler,
    	  			    	  success : function(re) {
    	  			    		if (re && re.ok) {
    	  			    			layer.alert("更新成功");
    	  			    			vueHandlerList.dataReload();
    	  			    		} else if (re && re.msg) {
    	  							layer.alert(re.msg);
    	  						}
    	  			    	  },
    	  			    	  fail : function(re) {
    	  			    		  layer.alert("出错了..." + re);
    	  			    	  }
                  	  });
                	  layer.close(index);
                  },
                  cancel: function () { //点击关闭按钮
                  }
				});
		},
		add_handler : function() {
			this.cur_hanlder = {}; // 重置表单
			layer.open({
				  type: 1,
				  shadeClose: true,
				  area: ['1000px', '700px'],
				  title: "详情",
				  border: [1],
				  content: $('#handler_detail'),
				  btn: ["保存"],
                  yes: function (index) {
                      if (vueHandlerList.cur_handler.name) {
                    	  vueHandlerList.cur_handler.enable = false;
                    	  if (vueHandlerList.cur_handler.ctype == "") {
                    		  vueHandlerList.cur_handler.ctype = "text";
                    	  }
                    	  if (vueHandlerList.cur_handler.priority == 0) {
                    		  vueHandlerList.cur_handler.priority = 100;
                    	  }
                    	  $.ajax({
                    		  url : base + "/qqrobot/admin/add",
      	  					  type : "post",
      	  			    	  dataType : "json",
      	  			    	  data : vueHandlerList.cur_handler,
      	  			    	  success : function(re) {
      	  			    		if (re && re.ok) {
      	  			    			layer.alert("新增成功");
      	  			    			vueHandlerList.dataReload();
      	  			    		} else if (re && re.msg) {
      	  							layer.alert(re.msg);
      	  						}
      	  			    	  },
      	  			    	  fail : function(re) {
      	  			    		  layer.alert("出错了..." + re);
      	  			    	  }
                    	  });
                      }
                      layer.close(index);
                  },
                  cancel: function () { //点击关闭按钮
                  }
				});
		},
		delete_handler: function(handler) {
			if (handler.enable) {
				layer.alert("请先禁用");
				return;
			}
			layer.confirm('确定要删除吗? 处理器名称是 ' + handler.name, {
				  btn: ['是的','我再想想'] //按钮
				}, function(pass, index){
					$.ajax({
              		  url : base + "/qqrobot/admin/delete",
	  					  type : "post",
	  			    	  dataType : "json",
	  			    	  data : {id:handler.id},
	  			    	  success : function(re) {
		  			    	//layer.close(index);
	  			    		if (re && re.ok) {
	  			    			layer.alert("删除成功");
	  			    			vueHandlerList.dataReload();
	  			    		} else if (re && re.msg) {
	  							layer.alert(re.msg);
	  						}
	  			    	  },
	  			    	  fail : function(re) {
	  			    		  layer.alert("出错了..." + re);
	  			    	  }
              	  });
				});
		}
	},
	created: function () {
	    this.dataReload();
    }
});