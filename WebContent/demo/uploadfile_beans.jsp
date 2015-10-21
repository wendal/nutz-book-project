<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="${base }/demo/upload/beans" method="post"  enctype="multipart/form-data">
	用户名<input name="user.name">
	id<input name="user.id">
	文件<input type="file" name="file">
	<button type="submit">提交</button>
</form>
<form action="${base}/demo/upload/beans2" method="post" enctype="multipart/form-data">
		
		<input name="user.name" value="胡涛"></br>
		<input name="user.age" value="23"></br>
		<input name="user.birthday" value="1991-01-06"></br>
		<input name="user.sex" value="true"></br>
		<input name="user.children[sb].name" value="女儿"></br>
		<input name="user.children[sb].age" value="13"></br>
		<input name="user.children[cnm].name" value="儿子"></br>
		<input name="user.children[cnm].age" value="12"></br></br>
		
		<input name="children[sb].name" value="老大"></br>
		<input name="children[sb].age" value="17"></br>
		<input name="children[cnm].name" value="老二"></br>
		<input name="children[cnm].age" value="18"></br>
		<input name="children[nsn].name" value="老三"></br>
		<input name="children[nsn].age" value="19"></br></br>
		
		<input name="cnmlgb" type="file"></br>
		<input name="cnmlgb" type="file"></br>
		<input name="cnmlgb" type="file"></br>
		
		<input type="submit">
	</form>
</body>
</html>