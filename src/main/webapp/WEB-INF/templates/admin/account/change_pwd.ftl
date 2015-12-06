<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<#include "/templates/admin/head.ftl"/>
<script type="text/javascript">
	$().ready(function() {
	    $("#jvForm").validate({
	        rules: {
	        	newpwd: {
	                required: true,
	                minlength: 5
	            },
	            repwd: {
	                required: true,
	                minlength: 5,
	                equalTo: "#newpwd"
	            }
	        },
	        messages: {
	        	newpwd: {
	                required: "<@s.m "login.input.request.password"/>",
	                minlength: jQuery.format("<@s.m "login.input.request.password.len"/>")
	            },
	            repwd: {
	                required: "<@s.m "login.input.request.password"/>",
	                minlength: "<@s.m "login.input.request.password.len"/>",
	                equalTo: "<@s.m "login.input.request.password.same"/>"
	            }
	        }
	    });
	});
	function updatepwd()
	{
		$.post("pwd_updata", {
			"oldpwd" : $('#oldpwd').val(),
			"newpwd" :  $('#newpwd').val(),
			"rewpwd" : $('#rewpwd').val()
 		}, function(data) {
			if (data.ok==true) {
				$.dialog({type: "success", content: '<@s.m "button.submit.success"/>', modal: true, autoCloseTime: 3000});
			} else {
				$.dialog({type: "error", content: data.msg, modal: true, autoCloseTime: 3000});
			}
	}, "json");
	}
</script>
</head>
<body>
<div class="box-positon">
	<div class="rpos"><@s.m "global.position"/>: <@s.m "cmsAdminGlobal.function"/> - <@s.m "global.add"/></div>
	<div class="clear"></div>
</div>
<div class="body-box">
<@p.form id="jvForm" action="pwd_updata" labelWidth="50" onsubmit="updatepwd();return false;">
<@p.password width="50" colspan="1" label="login.input.old.password" id="oldpwd" name="oldpwd" maxlength="100" class="required" required="true"/><@p.tr/>
<@p.password width="50" colspan="1" label="login.input.new.password" id="newpwd" name="newpwd" maxlength="100" class="required" required="true"/><@p.tr/>
<@p.password width="50" colspan="1" label="login.input.re.password" id="rewpwd" name="rewpwd" maxlength="100" class="required" required="true"/><@p.tr/>
<@p.td colspan="1"><@p.submit code="global.submit"/> &nbsp; <@p.reset code="global.reset"/></@p.td>
</@p.form>
</div>
</body>
</html>