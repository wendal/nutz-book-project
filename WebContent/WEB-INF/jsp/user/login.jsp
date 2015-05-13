<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>${msg['user.login.title']}</title>
    <jsp:include page="/WEB-INF/jsp/_include/_css_js.jsp"></jsp:include>
    <script type="text/javascript">
        var me = '<%=session.getAttribute("me")%>';
        var base = '${base}';
        $(function () {
            // 登陆
            $("#login_button").click(function () {
                // 提交数据
                $.ajax({
                    url: base + "/user/login",
                    type: "POST",
                    data: $('#loginForm').serialize(),
                    error: function (request) {
                        alert("Connection error");
                    },
                    dataType: "json",
                    success: function (data) {
                        if (data && data.ok) {
                            //alert("登陆成功");
                            window.location = base + "/home";
                        } else {
                            var emsg = data.msg;
                            if (emsg.substr(0, 3) == '验证码') {
                                var $captf = $('.with-captcha .md-text-field');
                                $captf.find('.err-tip').html(emsg);
                                $captf.addClass('has-err');
                                $captf.find('input').focus();
                                $captf.one('focusout', function () {
                                    $captf.removeClass('has-err');
                                });
                            } else {
                                alert(emsg);
                            }
                        }
                    }
                });
            });

            // 登出
//            $("#logout_button").click(function () {
//                window.location.href = base + "/user/logout"
//            });

            // 判断登陆状态
            if (me != "null") {
                $('#loginForm').hide();
            } else {
                $('#logoutForm').hide();
            }

            next_captcha();

            setTimeout(function () {
                $('input[name=username]').focus();
            }, 500);
        });
        function next_captcha() {
            $("#captcha_img").attr("src",
                    "${base}/captcha/next?w=120&h=48&_=" + new Date().getTime());
        }
    </script>
</head>
<body>
<div class="login-page-container paper z-depth-3 rounded">
    <div class="title">Welcome to NutzBook</div>
    <form class="body" id="loginForm" action="#" onsubmit="return false;">
        <div>
            <div class="md-text-field has-float-label">
                <label>${msg['user.login.username']}</label>
                <input type="text" name="username" nm="${msg['user.login.username']}" value="admin">
                <hr class="underline">
                <hr class="underline-focus">
                <div class="err-tip">用户名不能为空</div>
            </div>
        </div>
        <div>
            <div class="md-text-field has-float-label">
                <label>${msg['user.login.password']}</label>
                <input type="password" name="password" nm="${msg['user.login.password']}" value="123456">
                <hr class="underline">
                <hr class="underline-focus">
                <div class="err-tip">密码不能为空</div>
            </div>
        </div>
        <div class="with-captcha">
            <div class="md-text-field has-float-label">
                <label>${msg['user.login.captcha']}</label>
                <input name="captcha" type="text" value="" nm="${msg['user.login.captcha']}">
                <hr class="underline">
                <hr class="underline-focus">
                <div class="err-tip">验证码不能为空</div>
                <img id="captcha_img" onclick="next_captcha();return false;"/>
            </div>
        </div>
        <div>
            <div class="md-checkbox-field check-left">
                <label>${msg['user.login.rememberme']}</label>

                <div class="checkbox-wrap">
                    <i class="md-icon checked"></i>
                    <i class="md-icon unchecked"></i>
                    <input name="rememberMe" type="checkbox" nm="${msg['user.login.rememberme']}">
                </div>
            </div>
        </div>
        <div>
            <button id="login_button" class="md-button raised-button is-primary" type="submit">
                <span class="md-button-label">登 陆</span>
            </button>
        </div>
    </form>
    <form class="body" id="logoutForm" action="${base}/user/logout">
        <div>
            <h1>当前用户: <b><%=session.getAttribute("me")%></b></h1>
        </div>
        <div>
            <button id="logout_button" class="md-button raised-button is-primary" type="submit">
                <span class="md-button-label">登 出</span>
            </button>
        </div>
    </form>
</div>
</body>
</html>