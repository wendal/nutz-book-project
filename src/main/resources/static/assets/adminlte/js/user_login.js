
function do_login() {
	var username = $("#username").val();
	var password = $("#password").val();
	var captcha = $("#captcha").val();
	if (!username) {
		layer.alert("用户名是必填的");
		return;
	}
	if (!password) {
		layer.alert("密码是必填的");
		return;
	}
	if (!captcha) {
		layer.alert("验证码必填的");
		return;
	}
	$.ajax({
		url : base + "/user/login",
		type : "post",
		dataType : "json",
		data : $("#login_form").serialize(),
		success : function(re) {
			if (re && re.ok) {
				window.location = base + "/adminlte";
			} else if (re && re.msg) {
				layer.alert("登录失败: "+re.msg);
			}
		},
		fail : function (re) {
			layer.alert("fail: " + re);
		},
		error : function (err) {
			layer.alert("ERR: " + err);
		}
	});
}


