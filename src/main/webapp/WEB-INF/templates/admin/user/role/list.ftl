<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/templates/admin/head.ftl"/>
<script type="text/javascript">
function getTableForm() {
	return document.getElementById('tableForm');
}
</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "role.function"/> - <@s.m "global.list"/></div>
	<@shiro.hasPermission name="role:add">
	<form class="ropt">
		<input class="add" type="submit" value="<@s.m "global.add"/>" onclick="this.form.action='add';"/>
	</form>
	</@shiro.hasPermission>
	<div class="clear"></div>
</div>
<div class="body-box">
<@p.form id="tableForm" method="post">
<@p.hidden name="pageNumber" value="${pageNo!}"/>
<@p.table value=obj;role,i,has_next><#rt/>
	<@p.column title="ID" align="center">${role.id}</@p.column><#t/>
	<@p.column title="name">&nbsp;&nbsp;${role.name}</@p.column><#t/>
	<@p.column title="描述">&nbsp;&nbsp;${role.description!}</@p.column><#t/>
	<@shiro.hasPermission name="role:edit">
	<@p.column code="global.operate" align="center">
		<a href="edit?id=${role.id}" class="pn-opt"><@s.m "global.edit"/></a><#rt/>
	</@p.column><#t/>
	</@shiro.hasPermission>
</@p.table>
</@p.form>
</div>
</body>
</html>