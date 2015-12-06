<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>yvr's Control Panel - Powered by Rekoe</title>
<#include "/templates/admin/head.ftl"/>
<style type="text/css">        
*{margin:0;padding:0}
a:focus {outline:none;}
html{height:100%;overflow:hidden;}
body{height:100%;}
#top{ background-color:#1d63c6; height:71px; width:100%;}
.logo{width:209px; height:71px;}
.topbg{ height:71px;}
.login-welcome{padding-left:20px; color:#8099ae; font-size:12px;}
.login-welcome a:link,.login-welcome a:visited{color:#8099ae; text-decoration:none;}

#welcome {color: #8099ae;padding: 0 30px 0 5px;}
#logout {color: #8099ae; padding-left: 5px;}

.nav{height:28px; overflow:hidden;}
.nav-menu{ height:28px; list-style:none; font-size:13px;}
.nav .current {background: url(${base}/res/cms/img/admin/top_menu_selected.png) no-repeat center; color:#fff; text-align:center; width:100px; height:28px;} 
.nav .current a{color:#093153;}
.nav-menu li {height:28px;text-align:center; line-height:28px; float:left; }
.nav-menu li a{color:#fff; font-weight:bold;}
.nav-menu li.sep{background: url(${base}/res/cms/img/admin/top_menu_Divideline.png) no-repeat; width:3px; height:13px; margin:9px 0 0 0;}
.nav .normal{text-align:center; width:100px;}

.undis{display:none;}
.dis{display:block;}
</style>
<script type="text/javascript">
function g(o){
	return document.getElementById(o);
}
function HoverLi(m,n,counter){
	for(var i=1;i<=counter;i++){
		var a = g('tb_'+m+i);
		if(a != null)
		{
			a.className='normal';
		}
	}
	g('tb_'+m+n).className='current';
}
function redirectUrl(){
    window.location.href = "${base}/admin/logout"
}
$(function(){
	$('a').bind("focus", function(){   
	    $(this).blur();   
	}); 
});
</script>
</head>
<body>
<div id="top">
     <div class="top">
          <table width="100%" border="0" cellspacing="0" cellpadding="0" style="background:url(${base}/res/cms/img/admin/newtopbg2.jpg) repeat-x;">
          <tr>
              <td width="209"><div class="logo"><img src="${base}/res/cms/img/admin/logo.gif" width="209" height="71" /></div></td>
              <td valign="top" style="background:url(${base}/res/cms/img/admin/newtopbg1.jpg) no-repeat;">
                <div class="topbg">
                     <div class="login-welcome">
                         <table width="100%" border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <td width="320" height="38">
                                <img src="${base}/res/cms/img/admin/top_useinfo_icon_01.png"/>&nbsp;<span id="welcome"><@s.m code="global.admin.welcome"/> <@shiro.principal /></span>
                                <img src="${base}/res/cms/img/admin/top_useinfo_icon_02.png"/><a href="${base}/admin/logout" target="_top" id="logout" onclick="return confirm('<@s.m "global.confirm.logout"/>');"><@s.m "global.admin.logout"/></a>
                                </td>
                                <td align="right"></td>
                                <td width="100"></td>
                              </tr>
                            </table>
                       </div>  
                     <div class="nav" style="Z-INDEX:-1;">
                     	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <td></td>
                                <td>
                                	<#assign max = 14>
                                	<ul class="nav-menu">
                                    	<li class="current" id="tb_11" onclick="HoverLi(1,1,${max});"><a href="index_main" target="mainFrame"><@s.m "global.admin.home"/></a></li>
										<@perm_chow perm="user">	
										<li class="sep"></li><li class="normal" id="tb_12" onclick="HoverLi(1,2,${max});"><a href="frame/user_main" target="mainFrame">账号管理</a></li>
										</@perm_chow>
										<@perm_chow perm="topic">	
										<li class="sep"></li><li class="normal" id="tb_13" onclick="HoverLi(1,3,${max});"><a href="frame/topic_main" target="mainFrame">Topic管理</a></li>
										</@perm_chow>
										<#if obj=true>
										<li class="sep"></li><li class="normal" id="tb_14" onclick="HoverLi(1,4,${max});"><a href="frame/user_pwd_main" target="mainFrame">修改密码</a></li>
										</#if>
                                    </ul>
                                </td>
                              </tr>
                            </table>
                     </div>  
                </div>
          </tr>
        </table>
     </div>
</div>
<div class="top-bottom"></div>
</body>
</html>