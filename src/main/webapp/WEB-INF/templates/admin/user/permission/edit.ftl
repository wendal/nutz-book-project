<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/templates/admin/head.ftl"/>
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
<@p.hidden name="permission.id" value="${permission.id}" />
<@p.text colspan="1" width="45" label="global.name" name="name" required="true" value=permission.name class="required" maxlength="100"/><@p.tr/>
<tr>
	<th width="15%" class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span><@s.m "permissionCategory.name" />:</th>
	<td width="30%" class="pn-fcontent">
		<select name="permission.permissionCategoryId" >
			<#list obj as permissionCategory>
				<option value="${permissionCategory.id}"<#if permission.permissionCategoryId! == permissionCategory.id> selected="selected"</#if>>
					${permissionCategory.name}
				</option>
			</#list>
		</select>
	</td>
</tr>
<tr>
	<th width="15%" class="pn-flabel pn-flabel-h"><@s.m "permission.description" />:</th>
	<td width="30%" class="pn-fcontent"><textarea name="description" cols="50" rows="5" class="textbox">${permission.description!?html}</textarea></td>
</tr>
<@p.td colspan="2"><@p.submit code="global.submit"/> &nbsp; <@p.reset code="global.reset"/></@p.td>
</@p.form>
</div>
</body>
</html>