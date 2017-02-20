
// 辅助功能
function cs_qrcode() {
	setTimeout(function() {
		var tmp = "<img src="+ctxPath+"'/cs/qr?url=" + encodeURIComponent(window.location.href) + "'>";
    	layer.open({"title":"跨屏二维码有效期2分钟", shadeClose : true, "type":1, content:tmp, area : ['256px', '291px']}); // 256+35 = 291
	}, 300);
};
function f_signup() {
	setTimeout(function() {
    	layer.open({"title":"一步注册", shadeClose : true, "type":1, content : $("#signup_div"), area : ['300px', '370px']});
	}, 300);
};
function f_signin() {
next_captcha();
setTimeout(function() {
		layer.open({"title":"登陆", shadeClose : true, "type":1, content : $("#signin_div"), area : ['300px', '370px']});
	}, 300);
};

function do_signup() {
	$.ajax({
		url : ctxPath+"/yvr/u/signup",
		method : "POST",
		data : $("#signup_form").serialize(),
		dataType : "json",
		success : function(re) {
			if (console)
				console.log(re);
			layer.alert(re.msg || re.data);
		}
	});
	return false;
};

function do_signin() {
	$.ajax({
		url : ctxPath+"/user/login",
		method : "POST",
		data : $("#signin_form").serialize(),
		dataType : "json",
		success : function(re) {
			if (console)
				console.log(re);
			if (re.ok) {
				window.location.reload();
			} else {
				layer.alert(re.msg);
			}
		},
		error : function(jqXHR, exception) {
			if (jqXHR.status == 404) {
				// 如果已经登陆过,就会404,蛋疼的shiro
				window.location.reload();
			}
		}
	});
	return false;
}

function next_captcha() {
	$("#captcha_img").attr("src", ctxPath+"/captcha/next?w=120&h=48&_=" + new Date().getTime());
}

$(document).ready(function () {
  if($.support.pjax) {
  	$(document).pjax('a[data-pjax]', '#content', {fragment: '#content',maxCacheLength:0,timeout: 8000});
  }
  if (console)
  	console.log("^_^ 在找源码?这么巧. footer就有地址哦 https://github.com/wendal/nutz-book-project");
});

function startGuide(){
	var _intro = introJs();
	_intro.setOptions({
		'prevLabel':'&larr; 上一步',
		'nextLabel':'下一步 &rarr;',
		'skipLabel':'跳过',
		'doneLabel':'完成',
		//"scrollToElement":true,
		"showProgress":true,
		exitOnEsc : false,
		exitOnOverlayClick: false,
		scrollToElement : true
		}).start();
};

// 支付功能

      	function pay_tips(toUser, amount) {
      		if (amount) {
      			if (typeof(amount) == "string")
      				amount = parseInt(amount) * 100;
      		}
      		else {
      			amount = 0;
      		}
      		$.ajax({
      		    url : ctxPath+"/pay/bc/create",
      		    data : {"to":toUser, "amount":amount},
      		    dataType : "json",
      		    method : "post",
      		    success : function(re) {
      		    	if (re && re.data)
      		        	BC.click(re.data);
      		    }
      		});
      	}