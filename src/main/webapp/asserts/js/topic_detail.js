$(function(){
	var replyAddVue = new Vue({
		el : "#replyAdd",
		data : {
			replyContent : "",
			topicId : $("#input_topic_id").val()
		},
		methods : {
			replySubmit : function() {
				this.replyContent = this.replyContent.trim();
				if (this.replyContent.length < 2) {
					layer.alert("起码2个字才能说清楚,对吗?");
					return;
				}
				this.replyButtonTip = "正在提交...";
				var tmpData = {
						"content":this.replyContent
				};
				this.$http.post(ctxPath+"/yvr/t/"+this.topicId+"/reply", tmpData).then(function(resp){
					this.replyButtonTip = "我要答案";
					if (resp.ok) {
						var re = resp.json();
						if (re.ok) {
							this.replyButtonTip = "提交成功,正在跳转";
							window.location.href=ctxPath+"/yvr/t/" + this.topicId + "#" + re.data;
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
			replyAddImage : function () {
				layer.alert("图片上传暂时禁用");
			},
			replyAddCode : function() {
				layer.prompt({
					  formType: 2,
					  value: '',
					  title: '贴代码或日志',
					  maxlength : 1024000,
					  maxWidth : 1024
                }, function(value, index, elem){
					  console.log(value); //得到value
					  if (value) {
						  replyAddVue.replyContent += "\r\n```\r\n"+value+"\r\n```\r\n"
					  } else {
						  console.log("why?");
					  }
					  layer.close(index);
				});
			}
		}
	});
});