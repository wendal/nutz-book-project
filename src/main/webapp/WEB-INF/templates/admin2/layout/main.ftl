<#-- 带菜单的完整网页layout --->
<#macro admin2Layout title="Nutz Admin" admin_menu_level_1="" admin_menu_level_2="">
<#assign rsbase="${cdnbase!}${base}/rs/admin2">
<#assign adminbase="${base}/admin2">
<!DOCTYPE html>
<html lang="en">
<head>
	<#include "header.ftl">
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
              <li><a href="${adminbase}/user/profile.html"><i class="fa fa-user"></i> ${msg['user.profile.title']}</a></li>
              <li><a href="${adminbase}/user/setting.html"><i class="fa fa-cogs"></i> ${msg['user.setting.title']}</a></li>
              <li><a href="${adminbase}/user/logout"><i class="fa fa-sign-out"></i> ${msg['user.login.logout']}</a></li>
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
          <li><a href="${adminbase}/index.html"><i class="fa fa-home"></i> 公告板</a>
          </li>
          <li class="has_sub <#if admin_menu_level_1 == "${msg['admin_menu.users.level_one']}">open</#if>">
          	<a href="#">
          		<i class="fa fa-list-alt"></i> ${msg['admin_menu.users.level_one']}  <span class="pull-right"><i class="fa fa-chevron-right"></i></span>
          	</a>
            <ul>
              <li><a href="${adminbase}/user/users.html">用户管理</a></li>
              <li><a href="${adminbase}/user/roles.html">权限管理</a></li>
              <li><a href="${adminbase}/user/departments.html">部门管理</a></li>
            </ul>
          </li>
          <li class="has_sub <#if admin_menu_level_1 == "${msg['admin_menu.sys.level_one']}">open</#if>">
          	<a href="#">
          		<i class="fa fa-list-alt"></i> ${msg['admin_menu.sys.level_one']}  <span class="pull-right"><i class="fa fa-chevron-right"></i></span>
          	</a>
            <ul>
              <li><a href="${adminbase}/sys/basic.html">网站信息配置</a></li>
              <li><a href="${adminbase}/sys/email.html">邮件配置</a></li>
              <li><a href="${adminbase}/sys/push.html">推送配置</a></li>
              <li><a href="${adminbase}/sys/vars.html">系统变量</a></li>
            </ul>
          </li>      
		  <li class="has_sub <#if admin_menu_level_1 == "${msg['admin_menu.sysc.level_one']}">open</#if>">
          	<a href="#">
          		<i class="fa fa-list-alt"></i> ${msg['admin_menu.sysc.level_one']}  <span class="pull-right"><i class="fa fa-chevron-right"></i></span>
          	</a>
            <ul>
              <li><a href="${adminbase}/sysc/export.html">数据备份</a></li>
              <li><a href="${adminbase}/sysc/import.html">数据导入</a></li>
              <li><a href="${adminbase}/sysc/status.html">系统状态</a></li>
              <li><a href="${adminbase}/sysc/druid.html">Druid监控</a></li>
            </ul>
          </li>
          <li class="has_sub <#if admin_menu_level_1 == "${msg['admin_menu.yvr.level_one']}">open</#if>">
          	<a href="#">
          		<i class="fa fa-list-alt"></i> ${msg['admin_menu.yvr.level_one']}  <span class="pull-right"><i class="fa fa-chevron-right"></i></span>
          	</a>
            <ul>
              <li><a href="${adminbase}/yvr/topics.html">帖子管理</a></li>
              <li><a href="${adminbase}/yvr/replies.html">评论管理</a></li>
              <li><a href="${adminbase}/yvr/configure.html">论坛配置</a></li>
              <li><a href="${adminbase}/yvr/expstatic.html">静态化</a></li>
            </ul>
          </li>
          <li class="has_sub <#if admin_menu_level_1 == "${msg['admin_menu.openvpn.level_one']}">open</#if>">
          	<a href="#">
          		<i class="fa fa-list-alt"></i> ${msg['admin_menu.openvpn.level_one']}  <span class="pull-right"><i class="fa fa-chevron-right"></i></span>
          	</a>
            <ul>
              <li><a href="${adminbase}/openvpn/clients.html">客户端配置管理</a></li>
            </ul>
          </li>
        </ul>
    </div>

    <!-- Sidebar ends -->

  	<!-- Main bar -->
  	<div class="mainbar">

      <!-- Page heading -->
      <div class="page-head">
        <h2 class="pull-left"><i class="fa fa-home"></i> ${admin_menu_level_1}</h2>

        <!-- Breadcrumb -->
        <div class="bread-crumb pull-right">
          <a href="${adminbase}/index.html"><i class="fa fa-home"></i> Home</a> 
          <!-- Divider -->
          <span class="divider">/</span> 
          <a href="#" class="bread-current">${admin_menu_level_1}</a>
          <#if admin_menu_level_2 != "">
          <span class="divider">/</span> 
          <a href="#" class="bread-current">${admin_menu_level_2}</a>
          </#if>
        </div>

        <div class="clearfix"></div>

      </div>
      <!-- Page heading ends -->


	    <!-- Matter -->

	    <div class="matter">
        <div class="container">
          <div class="row">
            <div class="col-md-12">
              <#nested/>
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

<#include "footer.ftl">

<!-- Scroll to top -->
<span class="totop"><a href="#"><i class="fa fa-chevron-up"></i></a></span> 

</body>
</html>
</#macro>