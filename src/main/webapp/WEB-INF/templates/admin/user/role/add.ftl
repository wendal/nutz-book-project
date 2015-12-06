<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/templates/admin/head.ftl"/>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "cmsRole.group"/> - <@s.m "global.add"/></div>
	<form class="ropt">
		<input type="submit" value="<@s.m "global.backToList"/>" onclick="this.form.action='list';" class="return-button"/>
	</form>
	<div class="clear"></div>
</div>
<div class="body-box">
<@p.form id="jvForm" action="save" labelWidth="15" method="post">
<@p.text colspan="1" width="45" label="name" name="role.name" required="true" value="" class="required" maxlength="100"/><@p.tr/>
<@p.text colspan="1" width="45" label="描述" name="role.description" required="true" value="" class="required" maxlength="100"/><@p.tr/>
<#list obj as permissionCategory>
<#if permissionCategory.id!="2">
<tr class="authorities">
	<th width="15%" class="pn-flabel pn-flabel-h"><a href="javascript:;" class="selectAll" title="<@s.m "admin.role.selectAll" />">${permissionCategory.name}</a></th>
	<td width="30%" class="pn-fcontent">
		<span class="fieldSet">
		<#list permissionCategory.permissions as permission>
			<label><input value="${permission.id}" type="checkbox" name="authorities">${permission.description} </label>
		</#list>
		</span>
	</td>
</tr>
</#if>
</#list>
<@p.td colspan="2"><@p.submit code="global.submit"/> &nbsp; <@p.reset code="global.reset"/></@p.td>
</@p.form>
</div>
</body>
</html>