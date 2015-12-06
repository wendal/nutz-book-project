<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/templates/admin/head.ftl"/>
<style type="text/css">
.authorities label {
	min-width: 120px;
	_width: 120px;
	display: block;
	float: left;
	padding-right: 4px;
	_white-space: nowrap;
}
</style>
<script type="text/javascript">
$().ready(function() {
	var $inputForm = $("#jvForm");
	var $selectAll = $("#jvForm .selectAll");
	$selectAll.click(function() {
		var $this = $(this);
		var $thisCheckbox = $this.closest("tr").find(":checkbox");
		if ($thisCheckbox.filter(":checked").size() > 0) {
			$thisCheckbox.prop("checked", false);
		} else {
			$thisCheckbox.prop("checked", true);
		}
		return false;
	});
});
</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "admin.global.role.function"/> - <@s.m "global.add"/></div>
	<form class="ropt">
		<input type="submit" value="<@s.m "global.backToList"/>" onclick="this.form.action='list';" class="return-button"/>
	</form>
	<div class="clear"></div>
</div>
<div class="body-box">
<@p.form id="jvForm" action="update" labelWidth="15" method="post">
<@p.hidden name="role.id" value="${obj.id}" />
<@p.text colspan="1" width="45" label="name" name="role.name" required="true" value=obj.name class="required" maxlength="100"/><@p.tr/>
<@p.text colspan="1" width="45" label="描述" name="role.description" required="true" value=obj.description class="required" maxlength="100"/><@p.tr/>
<#list pcList as permissionCategory>
<#if permissionCategory.id!="2">
<tr class="authorities">
	<th width="15%" class="pn-flabel pn-flabel-h"><a href="javascript:;" class="selectAll" title="<@s.m "global.selectAll" />">${permissionCategory.name}</a></th>
	<td width="30%" class="pn-fcontent">
		<span class="fieldSet">
	<#if permissionCategory.permissions?exists>	
		<#list permissionCategory.permissions as permission>
			<label><input value="${permission.id}" type="checkbox" 
			<#list obj.permissions as p>
				<#if permission.id = p.id>
				 checked="checked"
				<#break>
				</#if>
			</#list>
			name="authorities">${permission.description!} </label>
		</#list>
	</#if>
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