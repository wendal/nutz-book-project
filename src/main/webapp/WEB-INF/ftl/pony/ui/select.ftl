<#--
<select><option></option></select>
-->
<#macro select
	list value="" multiple="" headerKey="" headerValue="" listKey="" listValue="" listDeep="" headerButtom="false"
	label="" noHeight="false" required="false" colspan="" width="100" help="" helpPosition="2" colon=":" hasColon="true"
	id="" name="" class="" style="" size="" title="" disabled="" tabindex="" accesskey=""
	vld=""
	onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
	>
<#include "control.ftl"/><#rt/>
<select<#rt/>
<#if id!=""> id="${id}"</#if><#rt/>
<#if multiple!=""> multiple="${multiple}"</#if><#rt/>
<#include "common-attributes.ftl"/><#rt/>
<#include "scripting-events.ftl"/><#rt/>
><#rt/>
<#if headerButtom=="false">
<#if headerKey!="" || headerValue!="">
	<option value="${headerKey}"<#if headerKey==value?string> selected="selected"</#if>><@s.mt code=headerValue text=headerValue/></option><#t/>
</#if>
</#if>
<#if list?is_sequence>
	<#if listKey!="" && listValue!="">
		<#if listDeep!="" && list?size gt 0><#local origDeep=list[0][listDeep]+1/></#if>
		<#list list as item>
			<option value="${item[listKey]}"<#if item[listKey]?string==value?string> selected="selected"</#if>><#if listDeep!="" && item[listDeep] gte origDeep><#list origDeep..item[listDeep] as i>&nbsp;&nbsp;</#list>></#if>${item[listValue]!}</option><#t/>
		</#list>
	<#else>
		<#list list as item>
			<option value="${item}"<#if item==value> selected="selected"</#if>>${item}</option><#t/>
		</#list>
	</#if>
<#else>
	<#list list?keys as key>
		<option value="${key}"<#if key==value?string> selected="selected"</#if>><@s.mt code=list[key] text=list[key]/></option><#t/>
	</#list>
</#if>
<#if headerButtom!="false">
<#if headerKey!="" || headerValue!="">
	<option value="${headerKey}"<#if headerKey==value> selected="selected"</#if>><@s.mt code=headerValue text=headerValue/></option><#t/>
</#if>
</#if>
</select>
<#include "control-close.ftl"/><#rt/>
</#macro>
