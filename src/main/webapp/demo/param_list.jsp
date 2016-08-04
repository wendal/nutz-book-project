<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${base}/rs/js/jquery.js"></script>
</head>
<body>
<form action="${base}/demo/param/list" method="post" enctype="application/x-www-form-urlencoded">
		
		<input name="user[0].name" value="ABC_Name_0"></br>
		<input name="user[0].id" value="0"></br>
		
		<input name="user[1].name" value="ABC_Name_1"></br>
		<input name="user[1].id" value="1"></br>
		
		<input name="user[2].name" value="ABC_Name_2"></br>
		<input name="user[2].id" value="2"></br>
		
		<input name="user[4].name" value="ABC_Name_4"></br>
		<input name="user[4].id" value="4"></br>
		
		<input name="user[3].name" value="ABC_Name_3"></br>
		<input name="user[3].id" value="3"></br>

		<input name="user[3].roles[0].name" value="ABC_ROLE_3"></br>
		<input name="user[3].roles[0].id" value="303"></br>
		
		<input type="submit">
	</form>
	
<form action="${base}/demo/param/mydate" method="post">
	<input name="mydate" value="2016-05-29 11:59:00">
		<input type="submit">
</form>
<p/>
<form action="${base}/demo/upload" method="post" enctype="multipart/form-data">
	<input name="file" type="file">
	<input type="submit">
</form>
<script type="text/javascript">
function getP() {
	$.ajax({
		url : "${base}/demo/uploadp",
		success : function (d) {
			if (d)
				console.log(d);
		},
		//async:false
	});
}
//setInterval (getP, 50);
</script>
<div>
	<audio src="${base}/demo/play/mp3" controls="controls"></audio>
</div>
</body>
</html>