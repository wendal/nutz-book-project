<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro"  uri="http://shiro.apache.org/tags"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="${base}/rs//favicon.ico">

    <title>Ask For Help</title>

    <!-- Bootstrap core CSS -->
    <link href="${base}/rs/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="${base}/rs/css/jumbotron.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">ASH社区</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <shiro:notAuthenticated>
          <form class="navbar-form navbar-right">
          	<div style="display: none;">
            <div class="form-group">
              <input type="text" placeholder="用户名" class="form-control" name="username">
            </div>
            <div class="form-group">
              <input type="password" placeholder="密码" class="form-control" name="password">
            </div>
            <div class="form-group">
              <input type="password" placeholder="验证码" class="form-control" name="captcha">
            </div>
            <div class="form-group">
              <img id="captcha_img" onclick="next_captcha();return false;" class="form-control"/>
            </div>
            <button type="button" class="btn btn-success">登陆</button>
            </div>
          	<a href="${base}/oauth/github" class="btn btn-success">Github登陆</a>
          </form>
          </shiro:notAuthenticated>
          <shiro:authenticated>
          	<form class="navbar-form navbar-right">
          	  <div class="form-group">
                <input type="text" class="form-control" placeholder="Search">
              </div>
          	  <button type="button" class="btn btn-success">Nutz官网</button>
          	  <button type="button" class="btn btn-success">新手入门</button>
          	  <button type="button" class="btn btn-success">定制Nutz</button>
          	  <button type="button" class="btn btn-info">收件箱</button>
          	  <button type="button" class="btn btn-danger">登出</button>
          	</form>
          </shiro:authenticated>
        </div><!--/.navbar-collapse -->
      </div>
    </nav>

    <!-- Main jumbotron for a primary marketing message or call to action -->
    <div class="jumbotron">
      <div class="container">
        <h2>欢迎光临!</h2>
        <shiro:notAuthenticated>
    		<div class="alert alert-warning alert-dismissible" role="alert">
    		<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
    		登陆后更精彩哦</div>
    	</shiro:notAuthenticated>
        <p style="display: none;"><a class="btn btn-primary btn-lg" href="http://nutzam.com" role="button">Nutz官网 &raquo;</a></p>
      </div>
    </div>
    
    

    <div class="container">
      <!-- Example row of columns -->
      <div class="row">
        <div class="col-md-9">
          <h2>Heading</h2>
          <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>
        </div>
        <div class="col-md-3">
          <div class="">
            <!-- 小登陆框及登陆后提示 -->
          	<div>
              <h3>ASH社区欢迎您</h3>
              <shiro:notAuthenticated>
                <a href="${base}/oauth/github" class="btn btn-success">Github登陆</a>
              </shiro:notAuthenticated>
              <shiro:authenticated>
                <p>您好, ${user.name}</p>
              </shiro:authenticated>
            </div>
            <hr>
            <!-- 最新回复5条 -->
            <div>
              <h3>最新回复</h3>
              <div id="last_replies"></div>
            </div>
            <hr>
            <!-- 未回复的最新5条记录 -->
            <div>
              <h3>尚未回复的帖子</h3>
              <div id="last_zero_replies"></div>
            </div>
          </div>
       </div>
      </div>

      <hr>

      <footer>
        <p>&copy; Wendal 2015 <a href="http://github.com/wendal/nutz-book-project" class="btn btn-default">本站源码</a>
        </p>
      </footer>
    </div> <!-- /container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script src="${base}/rs/js/bootstrap.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="${base}/rs/js/ie10-viewport-bug-workaround.js"></script>
    
    <script type="text/javascript">
    function next_captcha() {
        $("#captcha_img").attr("src", "${base}/captcha/next?w=120&h=48&_=" + new Date().getTime());
    };
    $(function() {
    	next_captcha();
	});
    </script>
  </body>
</html>
