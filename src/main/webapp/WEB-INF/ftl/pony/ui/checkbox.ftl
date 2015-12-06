<#--
<input type="checkbox"/>
-->
<#macro checkbox
	value="" cvalue="" labelFor="" readonly="" checked=""
	label="" noHeight="false" required="false" colspan="" help="" helpPosition="3"
	id="" name="" class="" style="" size="" title="" disabled="" tabindex="" accesskey=""
	onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
	>
<input type="checkbox"<#rt/>
 value="${value}"<#rt/>
 id="${id+'-'+name}"<#rt/>
<#if readonly!=""> readonly="${readonly}"</#if><#rt/>
<#if checked!=""> checked="${checked}"<#elseif cvalue!="" && cvalue==value> checked="checked"</#if><#rt/>
<#include "common-attributes.ftl"/><#rt/>
<#include "scripting-events.ftl"/><#rt/>
/><#if labelFor!=""><label for="${id+'-'+name}">${labelFor}</label></#if>
</#macro>
