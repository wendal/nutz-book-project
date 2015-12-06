<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/templates/admin/head.ftl"/>
<script type="text/javascript">

</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "admin.global.user"/> - <@s.m "global.add"/></div>
	<form class="ropt">
		<input type="submit" value="<@s.m "global.backToList"/>" onclick="this.form.action='list';" class="return-button"/>
	</form>
	<div class="clear"></div>
</div>
<div class="body-box">
<@p.form action="update" labelWidth="12">
<@p.hidden name="id" value="${obj.id}" />
<@p.td width="50" colspan="1" label="login.username">${obj.name}</@p.td><@p.tr/>
<@p.td colspan="1" label="admin.role.group">
<#list roleList as role>
<#assign isContain = false>
<#list obj.roles as urole>
	<#if role.id== urole.id>
		<#assign isContain = true>
		<#break>
	</#if>
</#list>
<label><input value="${role.id}" type="checkbox" name="roleIds" <#if isContain>checked="checked"</#if> />${role.name}</label><br />
</#list></@p.td><@p.tr/>
<@p.td colspan="1"><@p.submit code="global.submit"/> &nbsp; <@p.reset code="global.reset"/></@p.td><@p.tr/>
</@p.form>
</div>
</body>
</html>