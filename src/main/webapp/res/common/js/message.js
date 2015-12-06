function checkAll(){
	if($("#ids").attr("checked")){
		$("input[name='ids']").each(function(i){
			$(this).attr("checked","checked");
		 });
		}else{
			$("input[name='ids']").each(function(i){
				$(this).attr("checked","");
			 });
		}
}
//批量删除到垃圾箱
function toTrash(){
	var ids=new Array();
	$("input[name='ids']").each(function(i){
		if($(this).attr("checked")){
			ids.push($(this).val());
		}
	 });
	 if(ids.length>0){
		 if(!confirm("您确定要删除这些信息吗？")) {
				return;
			}
		 $.post("v_trash.do", {
				"ids" : ids
			}, function(data) {
				if(data.result){
					for(var i=0;i<ids.length;i++){
						$("#id_"+ids[i]).parent().parent().remove();
						}
					 $("#msgDiv").html("您选择的站内信息已被移动到垃圾箱 ");
				}else{
					alert("请先登录");
				}
			}, "json");
		 }else{
			 $("#msgDiv").html("请先选择信息");
			 }
}
//单条信息到垃圾箱
function trash(id){
	 if(!confirm("您确定要删除该条信息吗？")) {
			return;
		}
	 $.post("v_trash.do", {
			"ids" : id
		}, function(data) {
			if(data.result){
				$("#jvForm").attr("action","v_list.do");
				$("#jvForm").submit();
			}else{
				alert("请先登录");
			}
		}, "json");
}
function forward(){
	$("#jvForm").attr("action","v_forward.do");
	$("#jvForm").submit();
}
function empty(){
	var ids=new Array();
	$("input[name='ids']").each(function(i){
		if($(this).attr("checked")){
			ids.push($(this).val());
		}
	 });
	 if(ids.length>0){
		 if(!confirm("您确定要彻底删除这些信息吗？")) {
				return;
			}
		 $.post("v_empty.do", {
				"ids" : ids
			}, function(data) {
				if(data.result){
					for(var i=0;i<ids.length;i++){
						$("#id_"+ids[i]).parent().parent().remove();
						}
					 $("#msgDiv").html("您选择的站内信息已被彻底删除 ");
				}else{
					alert("请先登录");
				}
			}, "json");
		 }else{
			 $("#msgDiv").html("请先选择信息");
			 }
}
function emptySingle(id){
	 if(!confirm("您确定要彻底删除该信息吗？")) {
			return;
		}
	 $.post("v_empty.do", {
			"ids" : id
		}, function(data) {
			if(data.result){
				$("#jvForm").submit();
			}else{
				alert("请先登录");
			}
		}, "json");
}
function revert(){
	var ids=new Array();
	$("input[name='ids']").each(function(i){
		if($(this).attr("checked")){
			ids.push($(this).val());
		}
	 });
	 if(ids.length>0){
		 if(!confirm("您确定要还原这些信息吗？")) {
				return;
			}
		 $.post("v_revert.do", {
				"ids" : ids
			}, function(data) {
				if(data.result){
					for(var i=0;i<ids.length;i++){
						$("#id_"+ids[i]).parent().parent().remove();
						}
					 $("#msgDiv").html("您选择的站内信息已还原 ");
				}else{
					alert("请先登录");
				}
			}, "json");
		 }else{
			 $("#msgDiv").html("请先选择信息");
			 }
}
function toDraft(){
	$("#box").val(2);
	$("#nextUrl").val("v_list.do?box=2");
	$("#jvForm").attr("action","v_save.do");
	$("#jvForm").submit();
}
function toSend(){
	$("#nextUrl").val("v_list.do?box=1");
	$("#jvForm").attr("action","v_tosend.do");
	$("#jvForm").submit();
}
function reply(){
	$("#nextUrl").val("v_list.do?box=1");
	$("#jvForm").attr("action","v_reply.do");
	$("#jvForm").submit();
}
function find_user(){
	var username=$("#username").val();
	if(username!=""){
		$.post("v_findUser.do", {
			"username" : username
		}, function(data) {
			if(data.result){
				if(data.exist){
					$("#usernameMsg").html("没有此用户");
					$("#username").val("");
				}else{
					$("#usernameMsg").html("");
				}
			}else{
					alert("请先登录");
			}
		}, "json");
	}
}