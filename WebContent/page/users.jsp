<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="row">
	<form action="#" id="user_query_form">
		<div class="input-group">
			<span class="input-group-addon">过滤</span>
			<input type="text" name="name" class="form-contrl">
		</div>
		页数<input type="number" name="pageNumber" value="1">
		每页<input type="number" name="pageSize" value="10">
	</form>
	<button id="user_query_btn">查询</button>
	<p>---------------------------------------------------------------</p>
	<p id="user_count"></p>
	<div id="user_list">
		
	</div>
</div>
<div>
	<p>---------------------------------------------------------------</p>
</div>
<div id="user_add" class="row">
	<form action="#" id="user_add_form">
		用户名<input name="name">
		密码<input name="password">
	</form>
	<button id="user_add_btn">新增</button>
</div>


<script type="text/javascript">
	var pageNumber = 1;
	var pageSize = 10;
	var base = '<%=request.getAttribute("base")%>';
	function user_reload() {
		$.ajax({
			url : base + "/user/query",
			data : $("#user_query_form").serialize(),
			dataType : "json",
			success : function(data) {
				console.log(data);
				$("#user_count").html("共"+data.pager.recordCount+"个用户, 总计"+data.pager.pageCount+"页");
				var list_html = "";
				console.log(data.list);
				for (var i=0;i<data.list.length;i++) {
					var user = data.list[i];
					console.log(user);
					var tmp = "\n<p>" + user.id + " " + user.name
							  + " <button onclick='user_update(" + user.id +");'>修改</button> "
							  + " <button onclick='user_delete(" + user.id +");'>删除</button> "
							  + "</p>";
					list_html += tmp;
				}
				$("#user_list").html(list_html);
			}
		});
	}
	function myInit(args) {
		user_reload();
		$("#user_query_btn").click(function() {
			user_reload();
		});
		$("#user_add_btn").click(function() {
			$.ajax({
				url : base + "/user/add",
				data : $("#user_add_form").serialize(),
				dataType : "json",
				success : function(data) {
					if (data.ok) {
						user_reload();
						alert("添加成功");
					} else {
						alert(data.msg);
					}
				}
			});
		});
	};
	function user_update(userId) {
		var passwd = prompt("请输入新的密码");
		if (passwd) {
			$.ajax({
				url : base + "/user/update",
				data : {"id":userId,"password":passwd},
				dataType : "json",
				success : function (data) {
					if (data.ok) {
						user_reload();
						alert("修改成功");
					} else {
						alert(data.msg);
					}
				}
			});
		}
	};
	function user_delete(userId) {
		var s = prompt("请输入y确认删除");
		if (s == "y") {
			$.ajax({
				url : base + "/user/delete",
				data : {"id":userId},
				dataType : "json",
				success : function (data) {
					if (data.ok) {
						user_reload();
						alert("删除成功");
					} else {
						alert(data.msg);
					}
				}
			});
		}
	};
</script>