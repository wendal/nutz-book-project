<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h2>请选择用户</h2>
<form action="#">
	<c:forEach var="user" items="${obj['users']}">
		${user.name}<input type="checkbox" name="user" value="${user.name}">
	</c:forEach>
</form>
<h2>请选择角色</h2>
<form action="#">
	<c:forEach var="role" items="${obj['roles']}">
		${role.name}<input type="checkbox" name="role" value="${role.name}">
	</c:forEach>
</form>