<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户详情页</title>
<script type="text/javascript"
	src="http://cdn.staticfile.org/jquery/1.8.3/jquery.min.js"></script>
<script type="text/javascript">
	var base = '${base}';
	$.fn.serializeObject = function() {
		var o = {};
		var a = this.serializeArray();
		$.each(a, function() {
			if (o[this.name] !== undefined) {
				if (!o[this.name].push) {
					o[this.name] = [ o[this.name] ];
				}
				o[this.name].push(this.value || '');
			} else {
				o[this.name] = this.value || '';
			}
		});
		return o;
	};
	$(function() {
		$("#user_profile_btn").click(function() {
			//alert(JSON.stringify($("#user_profile").serializeObject()));
			$.ajax({
				url : base + "/user/profile/update",
				type : "POST",
				data : JSON.stringify($("#user_profile").serializeObject()),
				success : function() {
					location.reload();
				}
			});
		});
	});
</script>
</head>
<body>
	<div>
		<div>
			头像 <img alt="用户头像" src="${base}/user/profile/avatar">
			<p />
			<form action="${base}/user/profile/avatar" method="post"
				enctype="multipart/form-data">
				头像文件 <input type="file" name="file">
				<button type="submit">更新头像</button>
			</form>
			<span class="color:#f00"> <%
 	if (session.getAttribute("upload-error-msg") != null) {
 		String msg = session.getAttribute("upload-error-msg")
 				.toString();
 		out.print(msg);
 		session.removeAttribute("upload-error-msg");
 	}
 %>
			</span><p />
		</div>
	</div>
	<div>
		<form action="#" id="user_profile" method="post">
			<div>
				id:<c:out value="${obj.userId}"></c:out><p />
			</div>
			<div>
				昵称:<input name="nickname" value="${obj.nickname}"><p />
			</div>
			<div>
				邮箱:<input name="email" value="${obj.email}">
				<p />
			</div>
			<div>
				邮箱验证状态:<c:out value="${obj.emailChecked}"></c:out><p />
				<c:if test="${!obj.emailChecked}">
					<script type="text/javascript">
						function send_email_check() {
							$.ajax({
								url : base + "/user/profile/active/mail",
								type : "POST",
								dataType : "json",
								success : function (data) {
									if (data.ok) {
										alert("发送成功");
									} else {
										alert(data.msg);
									}
								}
							});
						}
					</script>
					<button type="button" onclick="send_email_check();return false;">发送验证邮件</button>
				</c:if>
			</div>
			<div>
				性别:<input name="gender" value="${obj.gender}"><p />
			</div>
			<div>
				自我介绍:<input name="description" value="${obj.description}"><p />
			</div>
			<div>
				地理位置:<input name="location" value="${obj.location}"><p />
			</div>
		</form>
		<button type="button" id="user_profile_btn">更新</button>
	</div>
</body>
</html>