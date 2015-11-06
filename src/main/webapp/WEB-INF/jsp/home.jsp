<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nutzbook 控制中心</title>
    <!--依赖-->
    <link rel="stylesheet" type="text/css" href="${base}/rs/css/normalize.css">
    <link rel="stylesheet" type="text/css" href="${base}/rs/css/animate.css">
    <link rel="stylesheet" type="text/css" href="${base}/rs/css/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" type="text/css" href="${base}/rs/css/font-md/css/material-design-iconic-font.css">
    <script type="text/javascript">
    	var home_base = '${base}';
    </script>
    <script src="${base}/rs/js/jquery.js"></script>
    <script src="${base}/rs/js/jquery.easing.js"></script>
    <script src="${base}/rs/js/underscore.js"></script>
    <script src="${base}/rs/js/zhttp.js"></script>
    <script src="${base}/rs/js/zutil.js"></script>
    <!-- 当前页面-->
    <link rel="stylesheet" type="text/css" href="${base}/rs/css/mdcss/md.css">
    <script src="${base}/rs/css/mdcss/md.js"></script>
    <link rel="stylesheet" type="text/css" href="${base}/rs/css/mdcss/page/home.css">
    <script src="${base}/rs/css/mdcss/page/home.js"></script>
    
    <!-- 弹出层layer -->
	<script src="${base}/rs/layer/layer.min.js" type="text/javascript"></script>
    <script>
        // home页的配置信息
        _md_page_home_ = {
            profile: {
                name: "${obj.nickname}",
                email: "${obj.email}",
                avatar : "${base}/user/profile/avatar"
            },
            closeQuick : true,
            nav_user: [
            /*{
                label: '登陆系统',
                icon: 'md-person',
                type: 'action',
                action: 'login'
            },*/
            {
                label: '安全退出',
                icon: 'md-person',
                type: 'action',
                action: 'logout'
            },
            {
                label: '跨屏二维码',
                icon: 'md-person',
                type: 'action',
                action: 'cs_qr'
            }
            ],
            nav_main: [{
                label: '用户信息',
                icon: 'md-cast',
                type: 'url',
                url: '/user_profile.jsp'
            }, {
                label: '用户管理',
                icon: 'md-cast-connected',
                type: 'url',
                url: '/users.jsp'
            }, {
                label: '权限管理',
                icon: 'md-event',
                type: 'url',
                url: '/authority.jsp'
            }, {
                label: '帖子管理',
                icon: 'md-event',
                type: 'url',
                url: '/topics.jsp'
            }, {
                label: 'Druid监控',
                icon: 'md-insert-chart',
                type: 'url',
                url: '/druid_static.jsp'
            }],
            actions: {
                'logout' : function() {
                	window.location.href = home_base + "/user/logout";
                },
                'cs_qr' : function() {
                	window.$mp.home.nav.close();
                	setTimeout(function() {
                		var tmp = "<img src='${base}/cs/qr?url=";
                		tmp += encodeURIComponent(window.location.href) +"'>";
                    	$.layer({title:"跨屏二维码有效期2分钟", type:1, time:100, page:{html:tmp}, area : ['256px', '291px']}); // 256+35 = 291
					}, 300);
                }
            }
        };
    </script>
</head>
<body>
<!--主菜单-->
<div id="md-page-header">
    <!--左边菜单-->
    <ul class="mp-header-left clearfix nav-btn-group">
        <li>
            <a href="#" onclick="return false;" class="md-icon-wrap md-icon-flip nav-switch">
                <i class="md-icon md-menu close-icon"></i>
                <i class="md-icon md-close open-icon"></i>
            </a>
        </li>
        <li id="logo">控制中心</li>
        <li class="md-icon-wrap module-arrow">
            <i class="md-icon md-chevron-right"></i>
        </li>
        <li id="module-label" labelBlock="back">
            <div class="front animated"></div>
            <div class="back animated"></div>
        </li>
    </ul>
</div>
<!--主操作区-->
<div id="md-page-main">
</div>
<div id="md-page-main-overlay"></div>
<!--导航-->
<div id="md-page-nav">
    <div class="nav-inner-scroll">
        <div class="nav-inner">
            <div class="user-profile">
                <div class="user-info">
                    <img class="user-avatar"></img>
                    <span class="user-name"></span>
                </div>
                <div class="user-menu-switch">
                    <span class="user-email"></span>
                    <i class="md-icon md-expand-more"></i>
                </div>
            </div>
            <!-- 用户相关操作 -->
            <ul id="user-menu"></ul>
            <!-- 系统操作 -->
            <ul id="main-menu"></ul>
        </div>
    </div>
</div>
<!--系统loading页面-->
<div id="md-system-loading">
</div>
<div id="md-system-loading-tip">
    <div class="spinner"></div>
    <div class="tip-label">系统加载中...</div>
</div>
</body>
</html>