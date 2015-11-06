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


<div class="row">
	<div class="col-xs-3 col-sm-2">
		<img alt="用户头像" src="${base}/user/profile/avatar" class="img-circle">
		<form action="${base}/user/profile/avatar" method="post" enctype="multipart/form-data" class="form-group">
			  <div class="form-group">
    			<label for="exampleInputFile">头像文件</label>
    			<input type="file" id="exampleInputFile" name="file">
    			<p class="help-block">可上传100kb以内的图片文件</p>
  			</div>
			<button type="submit" class="btn btn-default">更新头像</button>
		</form>
	</div>
	<div class="col-xs-6 col-sm-4">
	<form action="#" id="user_profile" method="post" class="form-horizontal">
		<label class="form-group">个人信息</label>
		<div class="form-group">
			<label for="input_nickname" class="col-sm-2 control-label">昵称</label>
    		<div class="col-sm-10">
    			<input name="nickname" id="input_nickname" class="form-control" placeholder="也许填一下真实名称">
    		</div>
		</div>
		<div class="form-group">
			<label for="input_email" class="col-sm-2 control-label">邮箱</label>
    		<div class="col-sm-4">
				<input name="email" class="form-control" id="input_email" placeholder="请填真实邮箱" type="email">
			</div>
			<label id="user_emailChecked" class="col-sm-4"></label>
			<div id="send_email_check" class="col-sm-4">
				<button type="button" onclick="send_email_check();return false;" class="btn btn-default">发送验证邮件</button>
			</div>
		</div>
		<div class="form-group">
			<label for="input_gender" class="col-sm-2 control-label">性别</label>
			<div class="col-sm-10">
				<select name="gender" class="form-control" id="input_gender">
					<option value="">未知</option>
					<option value="male">男</option>
					<option value="female">女</option>
					<option value="3rd">第三性别</option>
				</select>
			</div>
		</div>
		<div class="form-group">
			<label for="input_description" class="col-sm-2 control-label">自我介绍</label>
			<div class="col-sm-10">
				<input name="description" class="form-control" id="input_description" placeholder="写点什么吧">
			</div>
		</div>
		<div class="form-group">
			<label for="input_location" class="col-sm-2 control-label">地理位置</label>
			<div class="col-sm-10">
				<input name="location" class="form-control"  id="input_location" placeholder="where are you?">
			</div>
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
				var gender = profile.gender;
				if ("male" == gender) {
					$("option[value='male']").attr("selected", "selected");
				}
				else if ("female" == gender) {
					$("option[value='female']").attr("selected", "selected");
				}
				else if ("3rd" == gender) {
					$("option[value='3rd']").attr("selected", "selected");
				}
				$("input[name='description']").attr("value", profile.description);
				$("input[name='location']").attr("value", profile.location);
			}
		});
	}
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