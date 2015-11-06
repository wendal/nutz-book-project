<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head lang="en">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NutzBook 管理页面</title>
<jsp:include page="/WEB-INF/jsp/_include/_css_js.jsp"></jsp:include>
</head>
<body>
	<div id="div_nav" class="nav-container paper z-depth-3 rounded" align="right">
		<h2>欢迎来到Nutzbook</h2>
		<span>${msg['index.hi']}</span> <span id="index.user.name"></span>
		<a href="${base}/user/logout">${msg['index.logout']}</a>
	</div>
	<div id="div_menu" class="menubar-left">
		<div class="meanbar-list-item"><span>总览</span></div>
		<div>
			<span>用户管理</span>
		</div>
		<div class="meanbar-list-item">
			<span>权限管理</span>
		</div>
		<div class="meanbar-list-item">
			<span>系统管理</span>
		</div>
		<div class="meanbar-list-item">
			<span>流程管理</span>
		</div>
	</div>