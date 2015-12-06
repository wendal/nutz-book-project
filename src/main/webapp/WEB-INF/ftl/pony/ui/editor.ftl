<#--
<textarea name="textarea"></textarea>
-->
<#macro editor
	name value="" height="175"
	fullPage="false" toolbarSet="My"
	label="" noHeight="false" required="false" colspan="" width="100" help="" helpPosition="2" colon=":" hasColon="true"
	maxlength="65535"
	onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
	>
<#include "control.ftl"/><#rt/>
<textarea id="${name!?js_string}" name="${name!?js_string}" style="width:450px; height:150px">${value!}</textarea>
<script type="text/javascript">
new TINY.editor.edit('editor',{
	id:'${name!?js_string}',
	width:'900',
	height:140,
	cssclass:'te',
	controlclass:'tecontrol',
	rowclass:'teheader',
	dividerclass:'tedivider',
	controls:['bold','italic','underline','strikethrough','|','subscript','superscript','|',
			  'orderedlist','unorderedlist','|','outdent','indent','|','leftalign',
			  'centeralign','rightalign','blockjustify','|','unformat','|','undo','redo','n',
			  'font','size','style','hr','link','unlink'],
	footer:true,
	fonts:['Verdana','Arial','Georgia','Trebuchet MS'],
	xhtml:true,
	cssfile:'${base}/tinyeditor/style.css',
	bodyid:'editor',
	footerclass:'tefooter',
	toggle:{activetext:'wysiwyg',text:'source',cssclass:'toggle'},
	resize:{cssclass:'resize'}
});
</script>
<#include "control-close.ftl"/><#rt/>
</#macro>