<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>cms Administrator's Control Panel</title>
<#include "/templates/admin/head.ftl"/>
<script type="text/javascript">
if(top!=this) {
	top.location=this.location;
}
$(function() {
	$("#password").focus();
	$().ready( function() {
		var $loginForm = $("#jvForm");
		var $username = $("#username");
		var $password = $("#password");
		// 表单验证、记住用户名
		$loginForm.submit( function() {
			if ($username.val() == "") {
				$.message("warn", "请输入用户名");
				return false;
			}
			if ($password.val() == "") {
				$.message("warn", "请输入密码");
				return false;
			}
		});
		<#if obj??>
			$.message("error", "${obj}");
		</#if>
	});
});
</script>
<style type="text/css">
body{margin:0;padding:0;font-size:12px;background:url(${base}/res/cms/img/login/bg.jpg) top repeat-x;}
.input{width:150px;height:17px;border-top:1px solid #404040;border-left:1px solid #404040;border-right:1px solid #D4D0C8;border-bottom:1px solid #D4D0C8;}
.captcha {
width: 90px;
text-transform: uppercase;
ime-mode: disabled;
}
.captchaImage {
	margin-left: 10px;
	vertical-align: middle;
	cursor: pointer;
}
</style>
</head>
<body>
<form id="jvForm" action="${base}/admin/login" method="post">
<table width="750" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="200">&nbsp;</td>
  </tr>
  <tr>
    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="423" height="280" valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="${base}/res/cms/img/login/ltop.jpg" /></td>
              </tr>
              <tr>
                <td><img src="${base}/res/cms/img/login/llogo.png" /></td>
              </tr>
            </table></td>
          <td width="40" align="center" valign="bottom"><img src="${base}/res/cms/img/login/line.jpg" width="23" height="232" /></td>
          <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td height="90" align="center" valign="bottom"><img src="${base}/res/cms/img/login/ltitle.jpg" /></td>
              </tr>
              <tr>
                <td>
                <div>
				<#if obj??>
					<ul>
					<li>${obj}</li>
					</ul>
					<#else>
					<ul>
					<li><@shiro.guest>Hello guest!</@shiro.guest></li>
					</ul>
				</#if>
                </div>
                <table width="100%" border="0" align="center" cellpadding="0" cellspacing="5">
                    <tr>
                      <td width="91" height="40" align="right"><strong> <@s.m "login.username"/>：</strong></td>
                      <td width="211"><input type="input" id="username" name="username" value="" vld="{required:true}" maxlength="100" class="input"/></td>
                    </tr>
                    <tr>
                      <td height="40" align="right"><strong><@s.m "login.password"/>：</strong></td>
                      <td><input name="password" type="password" id="password" maxlength="32" vld="{required:true}" value="" class="input"/></td>
                    </tr>
                    <tr>
                      <td height="40" colspan="2" align="center">
					    <input type="image" src="${base}/res/cms/img/login/login.jpg" name="submit" />
                        &nbsp; &nbsp; <img name="reg" style="cursor: pointer" src="${base}/res/cms/img/login/reset.jpg" onclick="document.forms[0].reset()" /> </td>
                    </tr>
                  </table></td>
              </tr>
            </table></td>
        </tr>
      </table></td>
  </tr>
</table>
</form>
</body>
</html>
