<#if help!='' && helpPosition=='2'>
 <span class="pn-fhelp"><@s.mt code=help text=help/></span><#rt/>
<#elseif help!='' && helpPosition=='3'>
<div class="pn-fhelp"><@s.mt code=help text=help/></div><#rt/>
</#if>
<#if label!=''></td><#if colspan==''></tr><tr></#if></#if>