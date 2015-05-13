<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
		</span>
		<p />
	</div>
</div>
<div>
	<form action="#" id="user_profile" method="post">
		<div>
			昵称:<input name="nickname" value="${user_profile.nickname}">
			<p />
		</div>
		<div>
			邮箱:<input name="email" value="${user_profile.email}">
			<p />
		</div>
		<div>
			<span id="user_emailChecked"></span>
			<div id="send_email_check">
				<button type="button" onclick="send_email_check();return false;">发送验证邮件</button>
			</div>
		</div>
		<div>
			性别:<input name="gender" value="${user_profile.gender}">
			<p />
		</div>
		<div>
			自我介绍:<input name="description" value="${user_profile.description}">
			<p />
		</div>
		<div>
			地理位置:<input name="location" value="${user_profile.location}">
			<p />
		</div>
	</form>
	<button type="button" id="user_profile_btn">更新</button>
</div>


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
	function myInit(args) {
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
		$.ajax({
			url : base + "/user/profile/get",
			type : "GET",
			dataType : "json",
			success : function(profile) {
				$("input[name='nickname']").attr("value", profile.nickname);
				$("input[name='email']").attr("value", profile.email);
				if (profile.emailChecked) {
					$("#user_emailChecked").html("邮箱验证状态:已验证");
					$("#send_email_check").hide();
				} else {
					$("#user_emailChecked").html("邮箱验证状态:未验证");
					$("#send_email_check").show();
				}
				$("input[name='gender']").attr("value", profile.gender);
				$("input[name='description']").attr("value",
						profile.description);
				$("input[name='location']").attr("value", profile.location);
			}
		});
	};
	function send_email_check() {
		$.ajax({
			url : base + "/user/profile/active/mail",
			type : "POST",
			dataType : "json",
			success : function(data) {
				if (data.ok) {
					alert("发送成功");
				} else {
					alert(data.msg);
				}
			}
		});
	}
</script>