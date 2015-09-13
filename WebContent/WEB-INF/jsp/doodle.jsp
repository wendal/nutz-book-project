<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head lang="en">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>哪天空</title>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet" href="${base }/rs/css/bootstrap.css">
<link rel="stylesheet" href="${base }/rs/css/bootstrap-theme.css">
<link rel="stylesheet" href="${base }/rs/css/bootstrap-datepicker.css">

<script type="text/javascript" src="${base }/rs/js/jquery.js"></script>
<script type="text/javascript" src="${base }/rs/js/json2.js"></script>
<script type="text/javascript" src="${base }/rs/js/bootstrap.js"></script>
<script type="text/javascript" src="${base }/rs/js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="${base }/rs/locales/bootstrap-datepicker.zh-CN.min.js"></script>

<script type="text/javascript">
	var doodle_id = '${obj}';
	$(function() {
		$('#datetimepicker12').datepicker({
			inline : true,
			sideBySide : true,
			language : 'zh-CN',
			multidate : true,
			clearBtn : true,
			startDate : "2015/07/31",
			todayHighlight : true
		});
		$("#datetimepicker12").on("changeDate", function(event) {
		    $("#my_hidden_input").val(
		        $("#dates").datepicker('getFormattedDate')
		     )
		});
	});
</script>
</head>
<body>
	<div id="loaderDiv" style="width: 100%; margin: 0px; padding: 0px; z-index: 51; height: 5px;">&nbsp;</div>
	<form action="#">
		<div>
			<div class="container">
				<div class="form-group">
					<label for="exampleInputTitle">描述</label> <input type="text" class="form-control" id="exampleInputTitle" placeholder="必填" name="title">
				</div>
				<div class="form-group">
					<label for="exampleInputDescription">描述(可选)</label> <input type="text" class="form-control" id="exampleInputDescription" placeholder="可选" name="description">
				</div>
				<div class="form-group">
					<label for="exampleInputLocation">地点(可选)</label> <input type="text" class="form-control" id="exampleInputLocation" placeholder="可选" name="location">
				</div>
				<div class="form-group">
					<label for="exampleInputCreator">创建者(可选)</label> <input type="text" class="form-control" id="exampleInputCreator" placeholder="可选" name="creator">
				</div>
				<div class="form-group">
					<label for="exampleInputEmail1">Email(可选)</label> <input type="email" class="form-control" id="exampleInputEmail1" placeholder="可选" name="email">
				</div>
				<div class="panel panel-default">
					<div class="panel-heading" id="pickerHeader">
						可选日期
						<div id="dateCount"></div>
					</div>
					<div id="datetimepicker12"></div>
					<input type="hidden" id="dates" />
				</div>
			</div>
		</div>
	</form>


</body>
</html>