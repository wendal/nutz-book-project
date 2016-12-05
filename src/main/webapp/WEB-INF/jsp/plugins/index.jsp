<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>插件管理</title>
</head>
<body>
<div>
	<form action="${base}/plugins/add" enctype="multipart/form-data" method="post">
		标识码<input name="key">
		类名<input name="className">
		参数<input name="args">
		文件<input name="file" type="file">
		<button type="submit">提交</button>
	</form>
</div>

</body>
</html>