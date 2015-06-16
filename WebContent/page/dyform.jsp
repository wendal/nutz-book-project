<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- 用bootstrap先应付一下 -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>


<h2>动态表单</h2>
	<h4 id="form_count"></h4>
	<div class="panel panel-default">
		<form action="#" id="dyform_query_form">
			过滤<input type="text" name="name" class="form-contrl" onchange="dform_reload();">
			页数<input type="number" name="pageNumber" class="form-contrl" value="1" onchange="dform_reload();">
			每页<input type="number" name="pageSize" class="form-contrl" value="10" onchange="dform_reload();">
		</form>
	</div>
	<button class="btn" onclick="form_add();">新建</button>
<div class="panel panel-default">
	<div class="panel-heading">表单一览</div>
	<table class="table">
		<thead>
			<tr>
				<th>#</th>
				<th>名称</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody id="dyform_tbody">
		</tbody>
	</table>
</div>

<script type="text/javascript">
function dform_reload(){
	$.ajax({
		url : home_base + "/admin/form/list",
		data : $("#dyform_query_form").serialize(),
		dataType : "json",
		success : function(re) {
			if (re && re.ok) {
				var data = re.data;
				console.log(data);
				$("#form_count").html("共" + data.pager.recordCount + "个表单, 总计" + data.pager.pageCount + "页");
				var list_html = "";
				console.log(data.list);
				for (var i = 0; i < data.list.length; i++) {
					var form = data.list[i];
					console.log(form);
					var tmp = "<tr>";
					tmp += "<td>" + form.id + "</td>";
					tmp += "<td>" + form.name + "</td>";
					tmp += "<td>";
					tmp	+= " <button onclick='form_update(" + form.id + ");' class='btn btn-default'>修改</button> ";
					tmp	+= " <button onclick='form_preview(" + form.id + ");' class='btn btn-default'>预览</button> ";
					tmp	+= " <button onclick='form_clone(" + form.id + ");' class='btn btn-default'>克隆</button> ";
					tmp	+= " <button onclick='form_delete(" + form.id + ");' class='btn btn-default'>删除</button> ";
					tmp += "</td>";
					tmp += "</tr>";
					list_html += tmp;
				}
				$("#dyform_tbody").html(list_html);
			}
		}
	});
};

function form_add(){
	window.location.href = home_base + "/admin/form/design/";
};

function form_delete(form_id) {
	var s = prompt("请输入y确认删除");
	if (s == "y") {
		$.ajax({
			url : home_base + "/admin/form/delete",
			data : {id:form_id},
			success : dform_reload
		});
	}
};
function form_clone(form_id) {
	var n = prompt("请输入新表单的名词");
	if (n) {
		$.ajax({
			url : home_base + "/admin/form/clone",
			data : {id:form_id, name : n},
			success : dform_reload
		});
	}
};
function form_preview(form_id) {
	$.ajax({
		url : home_base + "/admin/form/preview/" + form_id,
		dataType : "json",
		success : function(re) {
			if (re && re.ok) {
				win_parse=window.open('','','width=800,height=600');
                //这里临时查看，所以替换一下，实际情况下不需要替换  
                //var data  = re.data.replace(/<\/+textarea/,'&lt;textarea');
                //win_parse.document.write('<textarea style="width:100%;height:100%">'+data+'</textarea>');
                win_parse.document.write('<div>'+re.data+'</div>');
                win_parse.focus();
			}
		}
	});
};

function form_update(form_id) {
	window.location.href = home_base + "/admin/form/design/" + form_id;
}

function myInit(args) {
	dform_reload();
};
</script>

