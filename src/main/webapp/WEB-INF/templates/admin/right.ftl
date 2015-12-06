<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>cms-left</title>
<#include "/templates/admin/head.ftl"/>
<script type="text/javascript">

$(function() {
	var msg = '欢迎登陆';
	$.message({type: "warn", content: msg});
});
</script>
</head>
<body>
  	    <div class="box-positon">
        	 <h1><@s.m "global.position"/>: <@s.m "global.admin.home"/> - <@s.m "global.admin.index"/></h1>
        </div>
<div class="body-box">
        <div class="welcom-con">
        	 <div class="we-txt">
             	  <p>
                  </p>
             </div>
             <ul class="ms">
             	<li class="wxx">访问量</li><li class="attribute"><@s.m "system.server.select"/>　　系统属性</li>
             </ul>
             <div class="ms-xx">
                 <div class="xx-xx">
                 </div>
                 <div class="attribute-xx" style="float:left">
                 	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                          <tr>
                            <td width="30%" height="30" align="right">操作系统版本：</td>
                            <td height="30"><span class="black">${props['os.name']!} ${props['os.version']!}</span></td>
                        </tr>
                          <tr>
                            <td width="30%" height="30" align="right">操作系统类型：</td>
                            <td height="30"><span class="black">${props['os.arch']!} ${props['sun.arch.data.model']!}位</span> </td>
                        </tr>
                          <tr>
                            <td width="30%" height="30" align="right">用户、目录、临时目录：</td>
                            <td height="30"><span class="black">${props['user.name']!}, ${props['user.dir']!}, ${props['java.io.tmpdir']!}</span></td>
                        </tr><tr>
                            <td width="30%" height="30" align="right">JAVA运行环境：</td>
                            <td height="30"><span>${props['java.runtime.name']!} ${props['java.runtime.version']!}</span></td>
                          </tr>
                          <tr>
                            <td width="30%" height="30" align="right">JAVA虚拟机：</td>
                            <td height="30"> <span>${props['java.vm.name']!} ${props['java.vm.version']!}</span></td>
                        </tr>
                   </table>  
               </div>

             </div>
             
  </div>
</body>
</html>