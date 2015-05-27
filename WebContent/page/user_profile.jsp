<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!-- 用bootstrap先应付一下 -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>


<div class="container">
	<div class="row">
		头像 <img alt="用户头像" src="${base}/user/profile/avatar">
		<p />
		<form action="${base}/user/profile/avatar" method="post"
			enctype="multipart/form-data">
			头像文件 <input type="file" name="file">
			<button type="submit" class="btn btn-default">更新头像</button>
		</form>
		<p />
	</div>
<div class="row">
	<form action="#" id="user_profile" method="post">
		<div class="input-group">
			<span class="input-group-addon">昵称</span>
			<input name="nickname" value="${user_profile.nickname}" class="form-control">
		</div>
		<div class="input-group">
			<span class="input-group-addon">邮箱</span>
			<input name="email" value="${user_profile.email}" class="form-control">
		</div>
		<div class="input-group">
			<span id="user_emailChecked"></span>
			<div id="send_email_check">
				<button type="button" onclick="send_email_check();return false;" class="btn btn-default">发送验证邮件</button>
			</div>
		</div>
		<div class="input-group">
			<span class="input-group-addon">性别</span>
			<input name="gender" value="${user_profile.gender}" class="form-control">
		</div>
		<div class="input-group">
			<span class="input-group-addon">自我介绍</span>
			<input name="description" value="${user_profile.description}" class="form-control">
		</div>
		<div class="input-group">
			<span class="input-group-addon">地理位置</span>
			<input name="location" value="${user_profile.location}" class="form-control">
		</div>
	</form>
	<button type="button" id="user_profile_btn"  class="btn btn-default">更新</button>
</div>
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
					if (profile.email) {
						$("#user_emailChecked").html("邮箱验证状态:未验证");
						$("#send_email_check").show();
					} else {
						$("#user_emailChecked").html("请更新邮箱地址");
						$("#send_email_check").hide();
					}
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