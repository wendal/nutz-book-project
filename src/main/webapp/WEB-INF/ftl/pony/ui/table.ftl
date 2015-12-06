<#--
表格标签：用于显示列表数据。
	value：列表数据，可以是Pagination也可以是List。
	class：table的class样式。默认"pn-ltable"。
	sytle：table的style样式。默认""。
	width：表格的宽度。默认100%。
-->
<#macro table value listAction="" class="pn-ltable" style="" theadClass="pn-lthead" tbodyClass="pn-ltbody" width="100%">
<table class="${class}" style="${style}" width="${width}" cellspacing="1" cellpadding="0" border="0">
<#if listAction=="">
	<#assign listAction="list">
</#if>
<#if value?is_sequence><#local pageList=value/><#else><#local pageList=value.list/></#if>
<#list pageList as row>
<#if row_index==0>
<#assign i=-1/>
<thead class="${theadClass}"><tr><#nested row,i,true/></tr></thead>
</#if>
<#assign i=row_index has_next=row_has_next/>
<#if row_index==0><tbody  class="${tbodyClass}"><tr onmouseover="this.bgColor='#eeeeee'" onmouseout="this.bgColor='#ffffff'"><#else><tr onmouseover="this.bgColor='#eeeeee'" onmouseout="this.bgColor='#ffffff'"></#if><#nested row,row_index,row_has_next/>
<#if !row_has_next>
</tr></tbody>
<#else>
</tr>
</#if>
</#list>
</table>
<#if !value?is_sequence>
<table width="100%" border="0" cellpadding="0" cellspacing="0"><tr><td align="center" class="pn-sp">
	共 ${value.totalCount} 条&nbsp;
	每页<input type="text" value="${value.pageSize}" style="width:30px" onfocus="this.select();" onblur="$.cookie('_cookie_page_size',this.value,{expires:3650});" onkeypress="if(event.keyCode==13){$(this).blur();return false;}"/>条&nbsp;
	<input class="first-page" type="button" value="首 页" onclick="_gotoPage('1');"<#if value.firstPage> disabled="disabled"</#if>/>
	<input class="pre-page" type="button" value="上一页" onclick="_gotoPage('${value.prePage}');"<#if value.firstPage> disabled="disabled"</#if>/>
	<input class="next-page" type="button" value="下一页" onclick="_gotoPage('${value.nextPage}');"<#if value.lastPage> disabled="disabled"</#if>/>
	<input class="last-page" type="button" value="尾 页" onclick="_gotoPage('${value.totalPage}');"<#if value.lastPage> disabled="disabled"</#if>/>&nbsp;
	当前 ${value.pageNo}/${value.totalPage} 页 &nbsp;转到第<input type="text" id="_goPs" style="width:50px" onfocus="this.select();" onkeypress="if(event.keyCode==13){$('#_goPage').click();return false;}"/>页
	<input class="go" id="_goPage" type="button" value="转" onclick="_gotoPage($('#_goPs').val());"<#if value.totalPage==1> disabled="disabled"</#if>/>
</td></tr></table>
<script type="text/javascript">
function _gotoPage(pageNo) {
	try{
		var tableForm = getTableForm();
		$("input[name=pageNumber]").val(pageNo);
		tableForm.action="${listAction}";
		tableForm.onsubmit=null;
		tableForm.submit();
	} catch(e) {
		alert(e);
		alert('_gotoPage(pageNo)方法出错');
	}
}
</script>
</#if>
</#macro>