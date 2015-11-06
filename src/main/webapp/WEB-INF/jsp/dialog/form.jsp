<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h2>请选择表单</h2>
<form action="#">
	<select>
		<c:forEach var="form" items="${obj}">
			<option value="${form.id}">${form.name}</option>
		</c:forEach>
	</select>
</form>