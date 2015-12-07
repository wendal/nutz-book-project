<#macro admin2Layout title="Nutz Admin">
<#assign rsbase="${cdnbase!}${base}/rs/admin2">
<#assign adminbase="${base}/admin2">
<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta charset="utf-8">
  <!-- Title and other stuffs -->
  <title>${title}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="${conf['website.long_description']}">
  <meta name="keywords" content="${conf['website.keywords']}">
  <meta name="author" content="wendal">
  <link rel="canonical" href="${conf["website.urlbase"]}" itemprop="url"/>


  <!-- Stylesheets -->
  <link href="${rsbase}/css/bootstrap.min.css" rel="stylesheet">
  <!-- Font awesome icon -->
  <link rel="stylesheet" href="${rsbase}/css/font-awesome.min.css"> 
  <!-- jQuery UI -->
  <link rel="stylesheet" href="${rsbase}/css/jquery-ui.css"> 
  <!-- Calendar -->
  <link rel="stylesheet" href="${rsbase}/css/fullcalendar.css">
  <!-- prettyPhoto -->
  <link rel="stylesheet" href="${rsbase}/css/prettyPhoto.css">  
  <!-- Star rating -->
  <link rel="stylesheet" href="${rsbase}/css/rateit.css">
  <!-- Date picker -->
  <link rel="stylesheet" href="${rsbase}/css/bootstrap-datetimepicker.min.css">
  <!-- CLEditor -->
  <link rel="stylesheet" href="${rsbase}/css/jquery.cleditor.css">  
  <!-- Data tables -->
  <link rel="stylesheet" href="${rsbase}/css/jquery.dataTables.css"> 
  <!-- Bootstrap toggle -->
  <link rel="stylesheet" href="${rsbase}/css/jquery.onoff.css">
  <!-- Main stylesheet -->
  <link href="${rsbase}/css/style.css" rel="stylesheet">
  <!-- Widgets stylesheet -->
  <link href="${rsbase}/css/widgets.css" rel="stylesheet">   
  
  <script src="${rsbase}/js/respond.min.js"></script>
  <!--[if lt IE 9]>
  <script src="${rsbase}/js/html5shiv.js"></script>
  <![endif]-->

  <!-- Favicon -->
  <link rel="shortcut icon" href="${rsbase}/img/favicon/favicon.png">
</head>

<body>

<div class="navbar navbar-fixed-top bs-docs-nav" role="banner">
  
    <div class="conjtainer">
      <!-- Menu button for smallar screens -->
      <div class="navbar-header">
		  <button class="navbar-toggle btn-navbar" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
			<span>Menu</span>
		  </button>
		  <!-- Site name for smallar screens -->
		  <a href="${adminbase}/index.html" class="navbar-brand hidden-lg">NutzCN</a>
		</div>
      
      

      <!-- Navigation starts -->
      <nav class="collapse navbar-collapse bs-navbar-collapse" role="navigation">
        <!-- Links -->
        <ul class="nav navbar-nav pull-right">
          <li class="dropdown pull-right">            
            <a data-toggle="dropdown" class="dropdown-toggle" href="#">
              <i class="fa fa-user"></i> ${me.nickname} <b class="caret"></b>              
            </a>
            
            <!-- Dropdown menu -->
            <ul class="dropdown-menu">
              <li><a href="${adminbase}/user/profile.html"><i class="fa fa-user"></i> Profile</a></li>
              <li><a href="${adminbase}/user/setting.html"><i class="fa fa-cogs"></i> Settings</a></li>
              <li><a href="${adminbase}/user/logout"><i class="fa fa-sign-out"></i> Logout</a></li>
            </ul>
          </li>
          
        </ul>
      </nav>

    </div>
  </div>


<!-- Header starts -->
  <header>
    <div class="container">
      <div class="row">

        <!-- Logo section -->
        <div class="col-md-4">
          <!-- Logo. -->
          <div class="logo">
            <h1><a href="#">Nutz<span class="bold">Admin</span></a></h1>
            <p class="meta">NutzCN后台管理系统</p>
          </div>
          <!-- Logo ends -->
        </div>

        <!-- Button section -->
        <div class="col-md-4">

          <!-- Buttons -->
          <ul class="nav nav-pills">

            <!-- Comment button with number of latest comments count -->
            <li class="dropdown dropdown-big">
              <a class="dropdown-toggle" href="#" data-toggle="dropdown">
                <i class="fa fa-comments"></i> Chats <span   class="label label-info">6</span> 
              </a>

                <ul class="dropdown-menu">
                  <li>
                    <!-- Heading - h5 -->
                    <h5><i class="fa fa-comments"></i> Chats</h5>
                    <!-- Use hr tag to add border -->
                    <hr />
                  </li>
                  <li>
                    <!-- List item heading h6 -->
                    <h6><a href="#">Hi :)</a> <span class="label label-warning pull-right">10:42</span></h6>
                    <div class="clearfix"></div>
                    <hr />
                  </li>
                  <li>
                    <h6><a href="#">How are you?</a> <span class="label label-warning pull-right">20:42</span></h6>
                    <div class="clearfix"></div>
                    <hr />
                  </li>
                  <li>
                    <h6><a href="#">What are you doing?</a> <span class="label label-warning pull-right">14:42</span></h6>
                    <div class="clearfix"></div>
                    <hr />
                  </li>                  
                  <li>
                    <div class="drop-foot">
                      <a href="#">View All</a>
                    </div>
                  </li>                                    
                </ul>
            </li>

            <!-- Message button with number of latest messages count-->
            <li class="dropdown dropdown-big">
              <a class="dropdown-toggle" href="#" data-toggle="dropdown">
                <i class="fa fa-envelope"></i> Inbox <span class="label label-primary">6</span> 
              </a>

                <ul class="dropdown-menu">
                  <li>
                    <!-- Heading - h5 -->
                    <h5><i class="fa fa-envelope"></i> Messages</h5>
                    <!-- Use hr tag to add border -->
                    <hr />
                  </li>
                  <li>
                    <!-- List item heading h6 -->
                    <h6><a href="#">Hello how are you?</a></h6>
                    <!-- List item para -->
                    <p>Quisque eu consectetur erat eget  semper...</p>
                    <hr />
                  </li>
                  <li>
                    <h6><a href="#">Today is wonderful?</a></h6>
                    <p>Quisque eu consectetur erat eget  semper...</p>
                    <hr />
                  </li>
                  <li>
                    <div class="drop-foot">
                      <a href="#">View All</a>
                    </div>
                  </li>                                    
                </ul>
            </li>

            <!-- Members button with number of latest members count -->
            <li class="dropdown dropdown-big">
              <a class="dropdown-toggle" href="#" data-toggle="dropdown">
                <i class="fa fa-user"></i> Users <span   class="label label-success">6</span> 
              </a>

                <ul class="dropdown-menu">
                  <li>
                    <!-- Heading - h5 -->
                    <h5><i class="fa fa-user"></i> Users</h5>
                    <!-- Use hr tag to add border -->
                    <hr />
                  </li>
                  <li>
                    <!-- List item heading h6-->
                    <h6><a href="#">Ravi Kumar</a> <span class="label label-warning pull-right">Free</span></h6>
                    <div class="clearfix"></div>
                    <hr />
                  </li>
                  <li>
                    <h6><a href="#">Balaji</a> <span class="label label-important pull-right">Premium</span></h6>
                    <div class="clearfix"></div>
                    <hr />
                  </li>
                  <li>
                    <h6><a href="#">Kumarasamy</a> <span class="label label-warning pull-right">Free</span></h6>
                    <div class="clearfix"></div>
                    <hr />
                  </li>                  
                  <li>
                    <div class="drop-foot">
                      <a href="#">View All</a>
                    </div>
                  </li>                                    
                </ul>
            </li> 

          </ul>

        </div>

        <!-- Data section -->

        <div class="col-md-4">
          <div class="header-data">

            <!-- Traffic data -->
            <div class="hdata">
              <div class="mcol-left">
                <!-- Icon with red background -->
                <i class="fa fa-signal bred"></i> 
              </div>
              <div class="mcol-right">
                <!-- Number of visitors -->
                <p><a href="#">7000</a> <em>visits</em></p>
              </div>
              <div class="clearfix"></div>
            </div>

            <!-- Members data -->
            <div class="hdata">
              <div class="mcol-left">
                <!-- Icon with blue background -->
                <i class="fa fa-user bblue"></i> 
              </div>
              <div class="mcol-right">
                <!-- Number of visitors -->
                <p><a href="#">3000</a> <em>users</em></p>
              </div>
              <div class="clearfix"></div>
            </div>

            <!-- revenue data -->
            <div class="hdata">
              <div class="mcol-left">
                <!-- Icon with green background -->
                <i class="fa fa-money bgreen"></i> 
              </div>
              <div class="mcol-right">
                <!-- Number of visitors -->
                <p><a href="#">5000</a><em>orders</em></p>
              </div>
              <div class="clearfix"></div>
            </div>                        

          </div>
        </div>

      </div>
    </div>
  </header>

<!-- Header ends -->

<!-- Main content starts -->

<div class="content">

  	<!-- Sidebar -->
    <div class="sidebar">
        <div class="sidebar-dropdown"><a href="#">Navigation</a></div>

        <!--- Sidebar navigation -->
        <!-- If the main navigation has sub navigation, then add the class "has_sub" to "li" of main navigation. -->
        <ul id="nav">
          <!-- Main menu with font awesome icon -->
          <li><a href="index.html"><i class="fa fa-home"></i> 公告板</a>
            <!-- Sub menu markup 
            <ul>
              <li><a href="#">Submenu #1</a></li>
              <li><a href="#">Submenu #2</a></li>
              <li><a href="#">Submenu #3</a></li>
            </ul>-->
          </li>
          <li class="has_sub"><a href="#"><i class="fa fa-list-alt"></i> 账号管理  <span class="pull-right"><i class="fa fa-chevron-right"></i></span></a>
            <ul>
              <li><a href="user/user.html">用户管理</a></li>
              <li><a href="user/role.html">权限管理</a></li>
              <li><a href="user/depament.html">部门管理</a></li>
            </ul>
          </li>  
          <li class="has_sub"><a href="#"><i class="fa fa-file-o"></i> 论坛管理  <span class="pull-right"><i class="fa fa-chevron-right"></i></span></a>
            <ul>
              <li><a href="yvr/topic.html">帖子管理</a></li>
              <li><a href="yvr/reply.html">评论管理</a></li>
              <li><a href="yvr/configure.html">论坛配置</a></li>
              <li><a href="yvr/expstatic.html">静态化</a></li>
            </ul>
          </li> 
          <li class="has_sub"><a href="#"><i class="fa fa-file-o"></i> 系统配置  <span class="pull-right"><i class="fa fa-chevron-right"></i></span></a>
            <ul>
              <li><a href="sys/basic.html">网站信息配置</a></li>
              <li><a href="sys/email.html">邮件配置</a></li>
              <li><a href="sys/push.html">推送配置</a></li>
              <li><a href="sys/vars.html">系统变量</a></li>
            </ul>
          </li>      
		  <li class="has_sub"><a href="#"><i class="fa fa-table"></i> 系统维护  <span class="pull-right"><i class="fa fa-chevron-right"></i></span></a>
            <ul>
              <li><a href="sysc/export.html">数据备份</a></li>
              <li><a href="sysc/import.html">数据导入</a></li>
              <li><a href="sysc/import.html">数据导入</a></li>
              <li><a href="sysc/status.html">系统状态</a></li>
              <li><a href="sysc/druid.html">Druid监控</a></li>
            </ul>
          </li> 
        </ul>
    </div>

    <!-- Sidebar ends -->

  	<!-- Main bar -->
  	<div class="mainbar">

      <!-- Page heading -->
      <div class="page-head">
        <h2 class="pull-left"><i class="fa fa-home"></i> Dashboard</h2>

        <!-- Breadcrumb -->
        <div class="bread-crumb pull-right">
          <a href="index.html"><i class="fa fa-home"></i> Home</a> 
          <!-- Divider -->
          <span class="divider">/</span> 
          <a href="#" class="bread-current">Dashboard</a>
        </div>

        <div class="clearfix"></div>

      </div>
      <!-- Page heading ends -->


	    <!-- Matter -->

	    <div class="matter">
        <div class="container">
          <div class="row">
            <div class="col-md-12">

              <div class="widget">
                <div class="widget-head">
                  <div class="pull-left">Title</div>
                  <div class="widget-icons pull-right">
                    <a href="#" class="wminimize"><i class="fa fa-chevron-up"></i></a> 
                    <a href="#" class="wclose"><i class="fa fa-times"></i></a>
                  </div>  
                  <div class="clearfix"></div>
                </div>
                <div class="widget-content">
                  <div class="padd">
                    <!-- Content goes here -->
                  </div>
                  <div class="widget-foot">
                    <!-- Footer goes here -->
                  </div>
                </div>
              </div>  
              
            </div>
          </div>
        </div>
		  </div>

		<!-- Matter ends -->

    </div>

   <!-- Mainbar ends -->	    	
   <div class="clearfix"></div>

</div>
<!-- Content ends -->

<!-- Footer starts -->
<footer>
  <div class="container">
    <div class="row">
      <div class="col-md-12">
            <!-- Copyright info -->
            <p class="copy">Copyright &copy; 2015 | <a href="https://nutz.cn">NutzCN</a> </p>
      </div>
    </div>
  </div>
</footer> 	

<!-- Footer ends -->

<!-- Scroll to top -->
<span class="totop"><a href="#"><i class="fa fa-chevron-up"></i></a></span> 

<!-- JS -->
<script src="${rsbase}/js/jquery.js"></script> <!-- jQuery -->
<script src="${rsbase}/js/bootstrap.min.js"></script> <!-- Bootstrap -->
<script src="${rsbase}/js/jquery-ui.min.js"></script> <!-- jQuery UI -->
<script src="${rsbase}/js/moment.min.js"></script> <!-- Moment js for full calendar -->
<script src="${rsbase}/js/fullcalendar.min.js"></script> <!-- Full Google Calendar - Calendar -->
<script src="${rsbase}/js/jquery.rateit.min.js"></script> <!-- RateIt - Star rating -->
<script src="${rsbase}/js/jquery.prettyPhoto.js"></script> <!-- prettyPhoto -->
<script src="${rsbase}/js/jquery.slimscroll.min.js"></script> <!-- jQuery Slim Scroll -->
<script src="${rsbase}/js/jquery.dataTables.min.js"></script> <!-- Data tables -->

<!-- jQuery Flot -->
<script src="${rsbase}/js/excanvas.min.js"></script>
<script src="${rsbase}/js/jquery.flot.js"></script>
<script src="${rsbase}/js/jquery.flot.resize.js"></script>
<script src="${rsbase}/js/jquery.flot.pie.js"></script>
<script src="${rsbase}/js/jquery.flot.stack.js"></script>

<!-- jQuery Notification - Noty -->
<script src="${rsbase}/js/jquery.noty.js"></script> <!-- jQuery Notify -->
<script src="${rsbase}/js/themes/default.js"></script> <!-- jQuery Notify -->
<script src="${rsbase}/js/layouts/bottom.js"></script> <!-- jQuery Notify -->
<script src="${rsbase}/js/layouts/topRight.js"></script> <!-- jQuery Notify -->
<script src="${rsbase}/js/layouts/top.js"></script> <!-- jQuery Notify -->
<!-- jQuery Notification ends -->

<script src="${rsbase}/js/sparklines.js"></script> <!-- Sparklines -->
<script src="${rsbase}/js/jquery.cleditor.min.js"></script> <!-- CLEditor -->
<script src="${rsbase}/js/bootstrap-datetimepicker.min.js"></script> <!-- Date picker -->
<script src="${rsbase}/js/jquery.onoff.min.js"></script> <!-- Bootstrap Toggle -->
<script src="${rsbase}/js/filter.js"></script> <!-- Filter for support page -->
<script src="${rsbase}/js/custom.js"></script> <!-- Custom codes -->
<script src="${rsbase}/js/charts.js"></script> <!-- Charts & Graphs -->
$(document).ready(function () {
  if($.support.pjax) {
  	$(document).pjax('a[data-pjax]', '#content', {fragment: '#content',maxCacheLength:0,timeout: 8000});
  }
  if (console)
  	console.log("^_^ 在找源码?这么巧. footer就有地址哦 https://github.com/wendal/nutz-book-project");
});
</body>
</html>
</#macro>