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

function lockUser(id,isLocked) {
	$.dialog({
		type: "warn",
		content: '<@s.m "admin.dialog.updateConfirm"/>',
		ok: '<@s.m "admin.dialog.ok"/>',
		cancel: '<@s.m "admin.dialog.cancel"/>',
		onOk: function() {
			$.ajax({
				url: "lock",
				type: "POST",
				data: {"id":id,"lock":isLocked},
				dataType: "json",
				cache: false,
				success: function(message) {
					$.message(message);
					if (message.type == "success") 
					{
						window.location.href = "list"
					}
				}
			});
		}
	});
}
</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "admin.global.user"/> - <@s.m "global.list"/></div>
	<@shiro.hasPermission name="user:add">
	<form class="ropt">
		<input class="add" type="submit" value="<@s.m "global.add"/>" onclick="this.form.action='add';"/>
	</form>
	</@shiro.hasPermission>
	<div class="clear"></div>
</div>
<div class="body-box">
<@p.form id="tableForm" method="post">
<@p.hidden name="pageNumber" value="${pageNo!}"/>
<@p.table value=obj;user,i,has_next><#rt/>
	<@p.column title="ID" align="center">${i+1}</@p.column><#t/>
	<@p.column code="login.username" align="center">${user.name}</@p.column><#t/>
	<@shiro.hasPermission name="user:update">
	<@p.column code="global.lock.status" align="center"><div id="lock_${user.id}"><#if user.locked><span style="color:red"><@s.m "global.true"/></span><#else><@s.m "global.false"/></#if></div></@p.column><#t/>
	<@p.column code="global.operate" align="center">
		<a href="edit?id=${user.id}" class="pn-opt"><@s.m "global.edit"/></a> <#rt/>
		<a href="javascript:void(0)" onclick="lockUser('${user.id}','<#if user.locked>false<#else>true</#if>')" class="pn-opt"><#if user.locked><span style="color:red"><@s.m "global.unlock"/></span><#else><@s.m "global.lock"/></#if></a><#t/>
	</@p.column><#t/>
	</@shiro.hasPermission>
</@p.table>
</@p.form>
</div>
</body>
</html>