$(function(){
	var topicAddVue = new Vue({
		el : "#topicAdd",
		data : {
			topicType : "ask",
			topicTitle : "",
			topicContent : "",
			topicButtonTip : "我要答案"
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
				var kvs = ["兽总", "在线等", "兽兽", "兽哥", "菜鸟", "急急"];
				for (var i=0;i<kvs.length;i++) {
					if (this.topicTitle.indexOf(kvs[i]) > -1) {
						layer.alert("标题含有禁止出现的字符["+kvs[i]+"],请修改措辞");
						return;
					}
				}
				this.topicContent = this.topicContent.trim();
				if (this.topicContent.length < 1) {
					layer.alert("内容总得写点啊");
					return;
				}
				this.topicButtonTip = "正在提交...";
				var tmpData = {"type":this.topicType,
						"title":this.topicTitle,
						"content":this.topicContent
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
				this.topicContent += "\r\n```\r\n这个位置贴代码或日志,不要移除前后标识符\r\n```";
			}
		}
	});

	
});
//topicAddVue.$watch("topicTitle", function(newVal, oldVal) {
//	console.log(newVal);
//});