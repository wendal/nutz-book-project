<#macro operateRight operate checkRight="true">
<#if operate?starts_with('/')>
	<#local opr=operate>
<#else>
	<#local opr=request.getRequestURI()?substring(base?length,request.getRequestURI()?last_index_of('/')+1) + operate>
</#if>
<#if ((Session._user_key)?? && Session._user_key==1) || ((Session._rights_key)?? && Session._rights_key.contains(opr))>
<#--
<#if "false"==checkRight || ((Session._rights_key)?? && Session._rights_key.contains(opr))>
-->
<#nested/>
</#if>
</#macro>