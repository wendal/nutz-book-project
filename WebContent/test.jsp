<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="test/p" method="post">
	<input name="user[0].name"     value="ABC">
	<input name="user[0].password" value="DEF">
	<input name="user[1].name"     value="ABC2">
	<input name="user[1].password" value="DEF2">
	<input type="submit">
</form>
</body>
</html>