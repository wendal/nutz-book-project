<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- 用bootstrap先应付一下 -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>



	<h2>帖子管理</h2>
	<div class="panel panel-default">
		<form action="#" id="topic_query_form">
			页数<input type="number" name="pageNumber" class="form-contrl" value="1" onchange="topic_reload();">
			每页<input type="number" name="pageSize" class="form-contrl" value="10" onchange="topic_reload();">
		</form>
	</div>
	<div class="panel panel-default">
		<div class="panel panel-default">
			<div class="panel-heading">帖子一览</div>
			<table class="table">
				<thead>
					<tr>
						<th>#</th>
						<th>名称</th>
						<th>类型</th>
						<th>标签</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody id="topic_list">

				</tbody>
			</table>
		</div>
	</div>

<script type="text/javascript">
	var pageNumber = 1;
	var pageSize = 10;
	var base = '<%=request.getAttribute("base")%>';
	function topic_reload() {
		$.ajax({
			url : base + "/yvr/admin/query",
			data : $("#topic_query_form").serialize(),
			dataType : "json",
			success : function(data) {
				console.log(data);
				$("#topic_count").html("共" + data.pager.recordCount + "个帖子, 总计" + data.pager.pageCount + "页");
				var list_html = "";
				console.log(data.list);
				for (var i = 0; i < data.list.length; i++) {
					var topic = data.list[i];
					var topic_id = "\"" + topic.id + "\"";
					//console.log(topic);
					if (topic.tags == undefined)
						topic.tags = "";
					var tmp = "<tr>";
					tmp += "<td>" + topic.id + "</td>";
					tmp += "<td>" + topic.title + "</td>";
					tmp += "<td>" + topic.type + "</td>";
					tmp += "<td>" + topic.tags + "</td>";
					tmp += "<td>";
					
					if (topic.top) {
						tmp	+= " <button onclick='topic_update(" + topic_id + ", false, \"top\");' class='btn btn-default'>取消置顶</button> ";
					} else {
						tmp	+= " <button onclick='topic_update(" + topic_id + ", true, \"top\");' class='btn btn-default'>置顶</button> ";
					}
					
					if (topic.good) {
						tmp	+= " <button onclick='topic_update(" + topic_id + ", false, \"good\");' class='btn btn-default'>取消加精</button> ";
					} else {
						tmp	+= " <button onclick='topic_update(" + topic_id + ", true, \"good\");' class='btn btn-default'>加精</button> ";
					}
					tmp	+= " <button onclick='topic_update_type(" + topic_id + ");' class='btn btn-default'>修改类型</button> ";
					tmp += " <button onclick='topic_delete(" + topic_id + ");' class='btn btn-default'>删除</button> ";
					tmp += " <button onclick='topic_update_tags(" + topic_id + ");' class='btn btn-default'>修改标签</button> ";
					tmp += "</td>";
					tmp += "</tr>";
					list_html += tmp;
				}
				$("#topic_list").html(list_html);
			}
		});
	}
	function myInit(args) {
		topic_reload();
		$("#topic_query_btn").click(function() {
			topic_reload();
		});
	}
	function topic_update(tid, val, tp) {
		var param = {id:tid, "opt":tp};
		param[tp] = val;
		$.ajax({
			method:"POST",
			url : base + "/yvr/admin/update",
			data : param,
			dataType : "json",
			success : function(data) {
				topic_reload();
			}
		});
	}
	function topic_update_type(tid) {
		var t = prompt("请输入新类型");
		if (t) {
			topic_update(tid, t, "type");
		}
	}
	function topic_delete(tid) {
		var t = prompt("真的要删除吗?");
		if (t == "y") {
			$.ajax({
				method:"POST",
				url : base + "/yvr/admin/delete",
				data : {id:tid},
				dataType : "json",
				success : function(data) {
					topic_reload();
				}
			});
		}
	}
	function topic_update_tags(tid) {
		var t = prompt("请输入新标签");
		if (t) {
			$.ajax({
				method:"POST",
				url : base + "/yvr/admin/update/tags",
				data : {"id":tid, "tags":t},
				dataType : "json",
				success : function(data) {
					topic_reload();
				}
			});
		}
	}
</script>