<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${msg['user.login.title']}</title>
<c:import url="/WEB-INF/jsp/head.jsp"></c:import>
<script type="text/javascript">
	var me = '<%=session.getAttribute("me")%>';
	var base = '${base}';
	$(function() {
		$("#login_button").click(function() {
			$.ajax({
				url : base + "/user/login",
				type : "POST",
				data : $('#loginForm').serialize(),
				error : function(request) {
					alert("Connection error");
				},
				dataType : "json",
				success : function(data) {
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
<script type="text/javascript">
	function next_captcha() {
		$("#captcha_img").attr("src",
				"${base}/captcha/next?_=" + new Date().getTime());
	}
</script>
</head>
<body>
	<h1>${msg['user.login.title']}</h1>
	<div class="example">
		<form action="#" id="loginForm" method="post">
			<div id="login_div" class="row">
				<div class="md-text-filed has-float-label">
					<label>${msg['user.login.username']}</label>
					<input type="text" name="username" nm="${msg['user.login.username']}" value="admin">
				</div>
				<div class="md-text-filed has-float-label">
					<label>${msg['user.login.password']}</label>
					<input type="password" name="password" nm="${msg['user.login.password']}" value="123456">
				</div>
				<div class="md-text-filed has-float-label">
					<label>${msg['user.login.captcha']}</label>
					<input name="captcha" type="text" value="" nm="${msg['user.login.captcha']}">
					<img id="captcha_img" onclick="next_captcha();return false;"
					src="${base}/captcha/next"></img>
				</div>
				<div class="md-text-filed has-float-label">
					<label>${msg['user.login.rememberme']}</label>
					<input name="rememberMe" type="checkbox" checked="checked" nm="${msg['user.login.rememberme']}">
				</div>
				<div>
					<button id="login_button" class="md-button raised-button">
						<span class="md-button-label">${msg['user.login.submit'] }</span>
					</button>
				</div>
			</div>
		</form>
		<div id="user_info_div">
			<p id="userInfo"></p>
			<a href="${base}/user/logout">登出</a>
		</div>
	</div>
</body>
</html>