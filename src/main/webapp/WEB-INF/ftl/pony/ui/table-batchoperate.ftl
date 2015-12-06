<#if (batchOperate?size > 0)>
<div class="pn-lbopt">
  <#list batchOperate as operate>  
    <@p.operateRight operate=operate['action'] checkRight=checkRight>
      <#if (operate.confirm)??>
	<input type="submit" value="${operate['name']}" onclick="if(confirm('${operate.confirm}')){this.form.action='${operate["action"]+actionSuffix}';return true;}else{return false;}"/>
      <#else>      
	<input type="submit" value="${operate['name']}" onclick="this.form.action='${operate["action"]+actionSuffix}';"/>
      </#if>
    </@p.operateRight>
  </#list>
  <#if wholeOptName!="">
&nbsp; <input type="submit" value="${wholeOptName}" onclick="this.form.action='${wholeOptAction+actionSuffix}';this.form.onsubmit=null;"/>
  </#if>
</div>
</#if>