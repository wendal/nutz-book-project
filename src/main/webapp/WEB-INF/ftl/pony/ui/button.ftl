<#--
<input type="button"/>
-->
<#macro button
	value="" code=""
	id="" name="" class="" style="" size="" title="" disabled="" tabindex="" accesskey=""
	onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
	>
<input type="button"<#rt/>
<#if value!=""> value="${value}"<#elseif code!=""> value="<@s.m code/>"</#if><#rt/>
<#if id!=""> id="${id}"</#if><#rt/>
<#include "common-attributes.ftl"/><#rt/>
<#include "scripting-events.ftl"/><#rt/>
/><#rt/>
</#macro>

<#--
<input type="submit"/>
-->
<#macro submit
	value="" code=""
	id="" name="" class="submit" style="" size="" title="" disabled="" tabindex="" accesskey=""
	onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
	>
<input type="submit"<#rt/>
<#if value!=""> value="${value}"<#elseif code!=""> value="<@s.m code/>"</#if><#rt/>
<#if id!=""> id="${id}"</#if><#rt/>
<#include "common-attributes.ftl"/><#rt/>
<#include "scripting-events.ftl"/><#rt/>
/><#rt/>
</#macro>

<#--
<input type="reset"/>
-->
<#macro reset
	value="" code=""
	id="" name="" class="reset" style="" size="" title="" disabled="" tabindex="" accesskey=""
	onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
	>
<input type="reset"<#rt/>
<#if value!=""> value="${value}"<#elseif code!=""> value="<@s.m code/>"</#if><#rt/>
<#if id!=""> id="${id}"</#if><#rt/>
<#include "common-attributes.ftl"/><#rt/>
<#include "scripting-events.ftl"/><#rt/>
/><#rt/>
</#macro>