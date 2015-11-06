<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- 用bootstrap先应付一下 -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>



	<h2>用户管理</h2>
	<div id="user_add" class="panel panel-default">
		<p id="user_count"></p>
		<form action="#" id="user_add_form">
			用户名<input type="text" name="name" class="form-contrl">
			密码   <input type="text" name="password" class="form-contrl">
			<button id="user_add_btn" type="button">新增</button>
		</form>
	</div>
	<div class="panel panel-default">
		<form action="#" id="user_query_form">
			过滤<input type="text" name="name" class="form-contrl" onchange="user_reload();">
			页数<input type="number" name="pageNumber" class="form-contrl" value="1" onchange="user_reload();">
			每页<input type="number" name="pageSize" class="form-contrl" value="10" onchange="user_reload();">
		</form>
	</div>
	<div class="panel panel-default">
		<div class="panel panel-default">
			<div class="panel-heading">用户一览</div>
			<table class="table">
				<thead>
					<tr>
						<th>#</th>
						<th>名称</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody id="user_list">

				</tbody>
			</table>
		</div>
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
				$("#user_count").html("共" + data.pager.recordCount + "个用户, 总计" + data.pager.pageCount + "页");
				var list_html = "";
				console.log(data.list);
				for (var i = 0; i < data.list.length; i++) {
					var user = data.list[i];
					console.log(user);
					var tmp = "<tr>";
					tmp += "<td>" + user.id + "</td>";
					tmp += "<td>" + user.name + "</td>";
					tmp += "<td>";
					tmp	+= " <button onclick='user_update(" + user.id
							+ ");' class='btn btn-default'>修改密码</button> "
							+ " <button onclick='user_delete(" + user.id
							+ ");' class='btn btn-default'>删除</button> ";
					tmp += "</td>";
					tmp += "</tr>";
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
	}
	function user_update(userId) {
		var passwd = prompt("请输入新的密码");
		if (passwd) {
			$.ajax({
				url : base + "/user/update",
				data : {
					"id" : userId,
					"password" : passwd
				},
				dataType : "json",
				success : function(data) {
					if (data.ok) {
						user_reload();
						alert("修改成功");
					} else {
						alert(data.msg);
					}
				}
			});
		}
	}
	function user_delete(userId) {
		var s = prompt("请输入y确认删除");
		if (s == "y") {
			$.ajax({
				url : base + "/user/delete",
				data : {
					"id" : userId
				},
				dataType : "json",
				success : function(data) {
					if (data.ok) {
						user_reload();
						alert("删除成功");
					} else {
						alert(data.msg);
					}
				}
			});
		}
	}
</script>