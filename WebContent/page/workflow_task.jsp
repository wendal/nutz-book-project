<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="shiro"  uri="http://shiro.apache.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- 用bootstrap先应付一下 -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<!-- 弹出层layer -->
<script src="${ctx}/rs/layer/layer.min.js" type="text/javascript"></script>

<h2>流程任务</h2>
	<h4 id="wftask_count"></h4>
	<div class="panel panel-default">
		<form action="#" id="wftask_query_form">
			过滤<input type="text" name="name" class="form-contrl" onchange="wftask_reload();">
			页数<input type="number" name="pageNumber" class="form-contrl" value="1" onchange="wftask_reload();">
			每页<input type="number" name="pageSize" class="form-contrl" value="10" onchange="wftask_reload();">
		</form>
	</div>
<div class="panel panel-default">
	<div class="panel-heading">流程任务一览</div>
	<table class="table">
		<thead>
			<tr>
				<th>#</th>
				<th>任务名称</th>
				<th>流程定义</th>
				<th>任务类型</th>
				<th>参与方式</th>
				<td>创建时间</td>
				<th>期望完成时间</th>
				<th>流程发起者</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody id="wftask_tbody">
		</tbody>
	</table>
</div>
<script type="text/javascript">
function wftask_reload(){
	$.ajax({
		url : home_base + "/admin/process/tasks",
		data : $("#wftask_query_form").serialize(),
		dataType : "json",
		success : function(re) {
			if (re && re.ok) {
				var data = re.data;
				//console.log(data);
				$("#wftask_count").html("共" + data.pager.recordCount + "个流程任务, 总计" + data.pager.pageCount + "页");
				var list_html = "";
				var orders = data.orders;
				var ps = data.ps
				var tasks = data.tasks;
				//console.log(data.list);
				for (var i = 0; i < tasks.length; i++) {
					var order = orders[i];
					var p = ps[i];
					var task = tasks[i];
					//console.log(wf);
					var tmp = "<tr>";
					tmp += "<td>" + task.id + "</td>";
					//tmp += "<td>" + task.version + "</td>";

					//tmp += "<td>" + task.name + "</td>";
					tmp += "<td>" + task.displayName + "</td>";
					
					if (p) {
						tmp += "<td>" + p.displayName + ".r" +  p.version + "</td>";
					} else {
						tmp += "<td>" + "未知流程" + "</td>";
					}
					
					if (task.taskType == 0) {
						tmp += "<td>" + "主办任务" + "</td>";
					} else {
						tmp += "<td>" + "协办任务" + "</td>";
					}
					
					if (task.performType == 0) {
						tmp += "<td>" + "普通任务" + "</td>";
					} else {
						tmp += "<td>" + "会签任务" + "</td>";
					}
					
					tmp += "<td>" + order.createTime + "</td>";
					
					if (task.expireTime) {
						tmp += "<td>" + task.expireTime + "</td>";
					} else {
						tmp += "<td>" + "无" + "</td>";
					}
					
					// 用户信息
					if (order.creator) {
						tmp += "<td>" + order.creator + "</td>";
					} else {
						tmp += "<td>" + "未知" + "</td>";
					}
					
					tmp += "<td>";
					tmp	+= " <button onclick='wftask_view(\"" + task.id + "\");' class='btn btn-default'>详情</button> ";
					tmp	+= " <button onclick='wftask_just_done(\"" + task.id + "\");' class='btn btn-default'>直接同意</button> ";
					tmp += "</td>";
					tmp += "</tr>";
					list_html += tmp;
				}
				$("#wftask_tbody").html(list_html);
			}
		}
	});
};

function myInit(args) {
	$("#wftask_update_div").hide();
	wftask_reload();
};
function wftask_just_done(wf_id) {
	$.ajax({
		type : "POST",
		url : home_base + "/admin/process/task/" + wf_id,
		data : "{}",
		dataType : "json",
		success : function (re) {
			alert("成功");
			wftask_reload();
		}
	});
};
</script>