<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>cms-left</title>
<#include "/templates/admin/head.ftl"/>
<script type="text/javascript">
    $(function(){
        Cms.lmenu('lmenu');
    });
</script>
</head>
<body class="lbody">
<div class="left">
<#include "/templates/admin/date.ftl"/>
	<div class="fresh">
		 <table width="100%" border="0" cellspacing="0" cellpadding="0">
	          <tr>
                <td align="right" width="47"><img src="${base}/res/cms/img/admin/sxicon.gif" /></td>
	            <td height="28" align="left" width="60">&nbsp;&nbsp;<a href="javascript:location.href=location.href"><@s.m "global.refresh"/></a></td>
	      	 </tr>
	     </table>
	</div>
</div>
</body>
</html>