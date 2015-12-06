<#--
<textarea name="textarea"></textarea>
-->
<#macro textarea
	wrap="" readonly="" cols="" rows="" value=""
	label="" noHeight="false" required="false" colspan="" width="100" help="" helpPosition="2" colon=":" hasColon="true"
	id="" name="" class="" style="" size="" title="" disabled="" tabindex="" accesskey=""
	vld="" equalTo="" maxlength="" minlength=""
	onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
	>
<#include "control.ftl"/><#rt/>
<textarea<#rt/>
<#if id!=""> id="${id}"</#if><#rt/>
<#if wrap!=""> wrap="${wrap}"</#if><#rt/>
<#if readonly!=""> readonly="${readonly}"</#if><#rt/>
<#if cols!=""> cols="${cols}"</#if><#rt/>
<#if rows!=""> rows="${rows}"</#if><#rt/>
<#include "common-attributes.ftl"/><#rt/>
<#include "scripting-events.ftl"/><#rt/>
><#if value!="">${value!?html}</#if></textarea>
<#include "control-close.ftl"/><#rt/>
</#macro>