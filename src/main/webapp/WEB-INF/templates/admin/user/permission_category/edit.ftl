<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/templates/admin/head.ftl"/>
<script type="text/javascript">
	$(function(){
	    $("#jvForm").validate();
	});
</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "permission.permissionCategory"/> - <@s.m "global.add"/></div>
	<form class="ropt">
		<input type="submit" value="<@s.m "global.backToList"/>" onclick="this.form.action='list';" class="return-button"/>
	</form>
	<div class="clear"></div>
</div>
<div class="body-box">
<@p.form id="jvForm" action="update" labelWidth="15" method="post">
<input type="hidden" name="id" value="${obj.id}" />
<@p.text colspan="1" width="45" label="cmsRole.name" name="name" required="true" value=obj.name class="required" maxlength="100"/><@p.tr/>
<@p.td colspan="2"><@p.submit code="global.submit"/> &nbsp; <@p.reset code="global.reset"/></@p.td>
</@p.form>
</div>
</body>
</html>