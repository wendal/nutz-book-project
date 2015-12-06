<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/templates/admin/head.ftl"/>
<script type="text/javascript">
	$().ready(function(){
    function redirectUrl(){
        window.location.href = "list.action"
    }
})
function getTableForm() {
	return document.getElementById('tableForm');
}
</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "admin.global.topic"/> - <@s.m "global.list"/></div>
	<div class="clear"></div>
</div>
<div class="body-box">
<@p.form id="tableForm" method="post">
<@p.hidden name="pageNumber" value="${pageNo!}"/>
<@p.table value=obj;topic,i,has_next><#rt/>
	<@p.column title="ID" align="center">${i+1}</@p.column><#t/>
	<@p.column title="标题" align="center">${topic.title}</@p.column><#t/>
	<@shiro.hasPermission name="topic:update">
	<@p.column code="global.operate" align="center">
		<a href="javascript:void(0)" onclick="Cms.deleted('${topic.id}')" class="pn-opt"><@s.m "global.delete" /></a><#t/>
	</@p.column><#t/>
	</@shiro.hasPermission>
</@p.table>
</@p.form>
</div>
</body>
</html>