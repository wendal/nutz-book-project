<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/template/admin/head.html"/>
<script type="text/javascript">
		$(function(){
			<#list m.roles as role>
				$("#c_${role.id}").attr("checked",true);
			</#list>
		});
		function setRole(o){
			var id = o.value;
			var u = ${m.id};
			var action = o.checked;
			var url='${base}/admin/admin/user/role';
			$.post(url,{id:u,role:id,action:action},function(data){
				if(!data.success){
					alert("<@s.m "cmsRole.setrole.err.function"/>);
				}
			},'json');
		}
</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "cmsAdminGlobal.function"/> - <@s.m "global.list"/></div>
	<div class="clear"></div>
</div>
<div class="body-box">
<form id="tableForm" method="post">
<input type="hidden" name="pageNumber" value="${pageNo!}"/>
<@p.table value=pagination;role,i,has_next><#rt/>
	<@p.column title="ID" align="center">${role.id}</@p.column><#t/>
	<@p.column code="cmsRole.name" align="center">${role.name}</@p.column><#t/>
	<@p.column code="global.operate" align="center">
	<input type="checkbox" value="${role.id}" onclick="setRole(this)" id="c_${role.id}"/>
	</@p.column><#t/>
</@p.table>
</form>
</div>
</body>
</html>