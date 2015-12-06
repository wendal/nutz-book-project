<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/template/admin/head.html"/>
<script type="text/javascript">
	$(function() {
		$("#jvForm").validate();
	});
</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "cmsAdminGlobal.function"/> - <@s.m "global.add"/></div>
	<form class="ropt">
		<input type="submit" value="<@s.m "global.backToList"/>" onclick="this.form.action='list.do';" class="return-button"/>
	</form>
	<div class="clear"></div>
</div>
<div class="body-box">
<#assign usernameExist><@s.m "error.usernameExist"/></#assign>
<@p.form id="jvForm" action="save_user" labelWidth="12">
<@p.text width="50" colspan="1" label="cmsUser.name" name="user.name" required="true" maxlength="100" vld="{required:true,username:true,remote:'check/username',messages:{remote:'${usernameExist}'}}"/><@p.tr/>
<@p.password width="50" colspan="1" label="cmsUser.password" id="user.password" name="user.password" maxlength="100" class="required" required="true"/><@p.tr/>
<@p.radio width="50" colspan="1" label="cmsUser.status" name="user.accountLocked" value="false" list={"true":"global.true","false":"global.false"} required="true" help="cmsUser.status.help"/><@p.tr/>
<@p.td colspan="1"><@p.submit code="global.submit"/> &nbsp; <@p.reset code="global.reset"/></@p.td>
</@p.form>
</div>
</body>
</html>