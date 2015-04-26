<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NutzBook demo</title>
<!-- 导入jquery -->
<script type="text/javascript" src="http://lib.sinaapp.com/js/jquery/2.0.3/jquery-2.0.3.min.js"></script>
<!-- 把user id复制到一个js变量 -->
<script type="text/javascript">
	var me = '<%=session.getAttribute("me") %>';
	var base = '${base}';
	$(function() {
		$("#login_button").click(function() {
			$.ajax({
				url : base + "/user/login",
				type: "POST",
				data:$('#loginForm').serialize(),
				error: function(request) {
					alert("Connection error");
				},
				dataType:"json",
				success: function(data) {
					if (data && data.ok) {
						alert("登陆成功");
						window.location = base + "/user/profile";
					} else {
						alert(data.msg);
					}
				}
			});
			return false;
		});
		if (me != "null") {
			$("#login_div").hide();
			$("#userInfo").html("您的Id是" + me);
			$("#user_info_div").show();
		} else {
			$("#login_div").show();
			$("#user_info_div").hide();
		}
	});
</script>
</head>
<body>
<div id="login_div">
	<form action="#" id="loginForm" method="post">
		用户名 <input name="username" type="text" value="admin">
		密码 <input name="password" type="password" value="123456">
		<script type="text/javascript">
			function next_captcha() {
				$("#captcha_img").attr("src", "${base}/captcha/next?_=" + new Date().getTime()); 
			}
		</script>
		验证码<input name="captcha" type="text" value="">
		<img id="captcha_img" onclick="next_captcha();return false;" src="${base}/captcha/next"></img>
		记住我<input name="rememberMe" type="checkbox" checked="checked">
		<button id="login_button">提交</button>
	</form>
</div>
<div id="user_info_div">
	<p id="userInfo"></p>
	<a href="${base}/user/logout">登出</a>
</div>
</body>
</html>