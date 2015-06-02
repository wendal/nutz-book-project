<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro"  uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html lang="zh">
    <head>
        <meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    	<meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>有问必答</title>
        
        <script type="text/javascript">
        	var home_base = "${base}";
        	var topicId = "${topic.id}";
        </script>
        
        <link rel="stylesheet" href="${base}/rs/css/ask.css" />
        <link rel="stylesheet" href="${base}/rs/editormd/css/editormd.css" />
        
        <script src="${base}/rs/editormd/jquery.min.js"></script>
        
        <script src="${base}/rs/editormd/lib/marked.min.js"></script>
        <script src="${base}/rs/editormd/lib/prettify.min.js"></script>
        
        <script src="${base}/rs/editormd/lib/raphael.min.js"></script>
        <script src="${base}/rs/editormd/lib/underscore.min.js"></script>
        <script src="${base}/rs/editormd/lib/sequence-diagram.min.js"></script>
        <script src="${base}/rs/editormd/lib/flowchart.min.js"></script>
        <script src="${base}/rs/editormd/lib/jquery.flowchart.min.js"></script>
        <script src="${base}/rs/editormd/editormd.js"></script>
        
        <!-- 用bootstrap先应付一下 -->
		<!-- Latest compiled and minified CSS -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
		<!-- Optional theme -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
		<!-- Latest compiled and minified JavaScript -->
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
		
		<script type="text/javascript" src="${base}/rs/layer/layer.min.js"></script>
		<script type="text/javascript" src="${base}/rs/laytpl/laytpl.js"></script>
        
        <script type="text/javascript" src="${base}/rs/js/ask.js"></script>

    </head>
    <body>

<div class="container-fluid" style="text-align: left; padding-right: 10px; padding-left: 10px;" >
	<div id="top_nav" class="row">
		<div class="col-md-2"></div>
		<div class="col-md-1">
			<img alt="" src="${base}/rs/favicon.ico" style="width: 30px; height: 30px;">
		</div>
		<div class="col-md-2">
			<input type="search" class="form-control" id="search_input" placeholder="输入需要搜索的关键字">
		</div>
		<div class="col-md-3"></div>
		<div class="col-md-2" id="nav_menu">
			<a href="${base}/ask/" class="btn btn-primary">首页</a>
			<a href="${base}/ask/newbeer.jsp" class="btn btn-primary">新手入门</a>
			<a href="#" class="btn btn-primary">关于</a>
			<shiro:authenticated>
				<a href="${base}/user/logout" class="btn btn-primary">登出</a>
			</shiro:authenticated>
			<shiro:notAuthenticated>
				<a href="#" id="login_link" class="btn btn-primary">登陆</a>
				<a href="${base}/oauth/github" id="login_link" class="btn btn-primary">Github登陆</a>
			</shiro:notAuthenticated>
			<shiro:hasRole name="admin">
				<a href="${base}/home" class="btn btn-primary">管理后台</a>
			</shiro:hasRole>
		</div>
	</div>
	<div class="row">
		<div class="col-md-2"></div>
		<div class="col-md-2">
			<a href="#tab_all" class="topic-tab current-tab">全部</a>
        	<a href="#tab_good" class="topic-tab ">精华</a>
        	<a href="#tab_share" class="topic-tab ">分享</a>
        	<a href="#tab_ask" class="topic-tab ">问答</a>
		</div>
		<div class="col-md-6"></div>
    </div>
    <div class="row">
    	<div class="col-md-2"></div>
		<div class="col-md-6">
			<div class="list-group" id="topic_list" style="display: none;"></div>
			<div id="topic_display" style="display: none;">
				<!-- 标题 -->
				<div>
					<h2>${topic.title}</h2>
				</div>
				<!-- 作者及一般信息 -->
				<div>
					<div><h4>作者:${topic.user.displayName}</h4></div>
					<div><h4>浏览:${topic.vistors}次</h4></div>
				</div>
				<!-- 文章内容 -->
				<div id="topic_view_body" class="jumbotron" style="padding-right: 15px; padding-left: 15px; background-color: silver;">
					<textarea>${topic.content}</textarea>
				</div>
				<!-- 回复信息 -->
				<div>
					<ol id="topic_replies"></ol>
				</div>
				<div id="topic_reply_div">
					<shiro:authenticated>
						<div class="form-group">
    						<textarea class="form-control" id="topic_reply_body" placeholder="支持md语法"></textarea>
  						</div>
						<button id="topic_reply_button">回复</button>
					</shiro:authenticated>
					<shiro:notAuthenticated>
						<a href="#">请先登录</a>
					</shiro:notAuthenticated>
				</div>
			</div>
		</div>
		<div class="col-md-2" id="right_link">
			<shiro:authenticated>
				<button id="ask_link">我要提问</button>
				<button id="cs_qr_link">跨屏二维码</button>
			</shiro:authenticated>
			<shiro:notAuthenticated>
				<button id="login_link">登录后可提问</button>
			</shiro:notAuthenticated>
		</div>
    </div>
	
</div>
	
<!-- 列表table的模板 -->
<script type="text/laytpl" id="ask_table_tpl">
{{# for(var i = 0, len = d.list.length; i < len; i++){ }}
<div class="row">
	<div class="col-md-1">
		<a class="user_avatar pull-left" href="/u/{{ d.list[i].user.id}}">
			<img src="${base}/user/profile/u/{{ d.list[i].user.id}}/avatar" title="{{ d.list[i].user.name}}" style="width: 30px; height: 30px;" class="img-rounded">
  		</a>
	</div>

	<div class="col-md-1">
		<span class="reply_count pull-left">
    	<span title="回复数" style="color: blue;">{{ d.list[i].replies}}</span>
    	<span >/</span>
    	<span title="点击数" style="color: red;">{{ d.list[i].vistors}}</span>
		</span>
	</div>
  	<div class="col-md-4" style="text-align: left;">
    <a class="topic_title" href="${base}/ask/v/{{d.list[i].id}}" title="{{d.list[i].title}}">
      {{d.list[i].title}}
    </a>
  </div>
</div>
{{# } }}
{{# for(var i = 1, len = d.pager.pageCount; i <= len; i++){ }}
	<button>第{{i}}页</button>
{{# } }}
</script>
<script type="text/laytpl" id="topic_reply_tpl">
{{# for(var i = 0, len = d.list.length; i < len; i++){ }}
<div class="row jumbotron" style="padding-right: 10px; padding-left: 10px; padding-top: 10px; padding-bottom: 10px;" >
	<div class="col-md-1">
		<a class="user_avatar pull-left" href="/u/{{ d.list[i].user.id}}">
			<img src="${base}/user/profile/u/{{ d.list[i].user.id}}/avatar" title="{{ d.list[i].user.name}}" style="width: 30px; height: 30px;" class="img-rounded">
  		</a>
	</div>
	<div id="reply_md_{{ i}}" class="col-md-5">
		<textarea>{{ d.list[i].content}}</textarea>
	</div>
</div>
{{# } }}
</script>
    
    
<!-- 
提问弹出层
 -->    
    <div id="ask_md_div" style="display: none;" class="container">
        <div id="layout">
            <header>
                <h1>我要提问:</h1>
                <input type="text" id="new_topic_title">
                <button id="ask_submit_button">提交</button>
            </header>
            <div id="test-editormd">
            	<!-- 里面放示例 -->
                <textarea style="display:none;" id="new_topic_content">
#### 环境

- nutz版本
- 系统
- java版本
- 特别值得一提的jar

#### 代码片段

```java
@At
public void insert(@Param("...")Pet pet) {
    dao.insert(pet);
}
```

### 日志截取

```log
DEBUG - loading ioc js config from [dao.js]
```
</textarea>
            </div>
        </div>
	</div>

    </body>
</html>