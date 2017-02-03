$(function(){
	var topicAddVue = new Vue({
		el : "#topicAdd",
		data : {
			topicType : "ask",
			topicTitle : "",
			topicContent : "",
			topicButtonTip : "我要答案(提交)",
			topicTag : "",
			suggestions : []
		},
		methods : {
			topicSubmit : function() {
				this.topicTitle = this.topicTitle.trim();
				if (this.topicTitle.length < 10) {
					layer.alert("标题起码10个字");
					return;
				}
				if (this.topicTitle.length > 100) {
					layer.alert("标题最多100个字符");
					return;
				}
				var kvs = ["兽总", "在线等", "兽兽", "兽哥", "菜鸟", "急急", "揪心", "求帮忙", "为毛", "急需", "弱弱", "兽懂", "新手"]; // 这样下去我得弄个表了...
				for (var i=0;i<kvs.length;i++) {
					if (this.topicTitle.indexOf(kvs[i]) > -1) {
						layer.alert("标题含有["+kvs[i]+"],请修改措辞");
						return;
					}
				}
				this.topicTitle = this.topicTitle.replace(" 【", "[").replace("】", "]");
				this.topicContent = this.topicContent.trim();
				if (this.topicContent.length < 10) {
					layer.alert("起码10个字才能说清楚问题,对吗? 越详尽的内容,越快解决问题!");
					return;
				}
				this.topicButtonTip = "正在提交...";
				var tmpData = {
						"type":this.topicType,
						"title":this.topicTitle,
						"content":this.topicContent,
						"_tags" : this.topicTag
				};
				this.$http.post(ctxPath+"/yvr/add", tmpData).then(function(resp){
					this.topicButtonTip = "我要答案";
					if (resp.ok) {
						var re = resp.json();
						if (re.ok) {
							this.topicButtonTip = "提交成功,正在跳转";
							window.location.href=ctxPath+"/yvr/t/" + re.data;
						} else {
							layer.alert(re.msg);
						}
					} else {
						layer.alert("网络或系统错误:"+resp);
					}
				}, function(resp) {
					layer.alert("网络或系统错误:"+resp);
				});
			},
			topicAddImage : function () {
				layer.alert("图片上传暂时禁用");
			},
			topicAddCode : function() {
				layer.prompt({
					  formType: 2,
					  value: '',
					  title: '贴代码或日志',
					  maxlength : 1024000,
					  maxWidth : 1024
                }, function(value, index, elem){
					  console.log(value); //得到value
					  if (value) {
						  topicAddVue.topicContent += "\r\n```\r\n"+value+"\r\n```\r\n"
					  } else {
						  console.log("why?");
					  }
					  layer.close(index);
				});
			}
		}
	});

	topicAddVue.$watch("topicTitle", function(newVal, oldVal) {
	   this.$http.get(ctxPath+"/yvr/search", {"params":{"q":newVal,"format":"json"}}).then(function(resp){
		   if (!resp.ok)
			   return;
		   this.suggestions = resp.json().suggestions;
	   });
	});
});