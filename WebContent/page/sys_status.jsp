<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
    

<!-- 用bootstrap先应付一下 -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>



<div class="panel panel-default">
	<div class="panel-heading">缓存状态</div>
	<table class="table">
		<thead>
			<tr>
				<th>#</th>
				<th>名称</th>
				<th>状态概述</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody id="cacheStat_tbody">
		</tbody>
	</table>
</div>
<script type="text/javascript">
function loadStat() {
	$.ajax({
		url : home_base + "/sys/stat/cache",
		dataType : "json",
		success : function (re) {
			console.log(re);
			if (re && re.ok) {
				var cacheStats = re.data;
				var tmp = "";
				for (var i = 0; i < cacheStats.length; i++) {
					var cacheStat = cacheStats[i];
					tmp += "<tr>";
					tmp += "<td>" + i + "</td>";
					tmp += "<td>" + cacheStat.name + "</td>";
					tmp += "<td>";
					tmp += "总大小:" + cacheStat.getSize + ",穿透率:";
					if (cacheStat.cacheMissCount > 0) {
						tmp += (cacheStat.cacheMissCount * 100 / (cacheStat.cacheHitCount + cacheStat.cacheMissCount)) + "%";
					} else {
						tmp += "0%";
					}
					tmp += "</td>";
					tmp += "<td>";
					tmp += "<button class='btn btn-default'>" + "详情" + "</button>";
					tmp += "</td>";
					
					
					
					tmp += "</tr>\n";
				}
				console.log(tmp);
				$("#cacheStat_tbody").html(tmp);
				window._sys_cache_stats = cacheStats;
			}
		},
		error : function (re, a1, a2) {
			console.log(re);
			console.log(a1);
			console.log(a2);
		}
	});
}
function myInit(args) {
	loadStat();
};
</script>