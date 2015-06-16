
// 禁掉window.showModalDialog
if (console && console.log) {
	console.log("disable window.showModalDialog");
}
window.showModalDialog = function(){};

// 覆盖snakerflow默认的选择器,是在太蛋疼了
$(function() {
	var _f = function(arg){
		var _props,_k,_div,_src,_r;
		this.init = function(props, k, div, src, r){
			_props=props; _k=k; _div=div; _src=src; _r=r;
			//console.log(props[_k].value);
			//console.log(props[arg].value);
			//console.log(props);

			$('<input style="width:88%;" readonly="true" id="' + arg +'_dialogEditor"/>').val(props[arg].value).appendTo('#'+_div);
			$('<input style="width:10%;" type="button" value="选择"/>').click(function(){
				//alert("选择:" + snakerflow.config.ctxPath + arg);
				var element = document.getElementById(arg + "_dialogEditor");
				console.log(arg);
				var l  = _snakerflow_dialog(arg);
				if (l == null )
					return;
				var result = splitUsersAndAccounts(l);
				element.title = result[1];
				element.value = result[1];
				//props[_k].value = result[1];
				props[arg].value = result[0];
			}).appendTo('#'+_div);

			$('#'+_div).data('editor', this);
		}
		this.destroy = function(){
			//
		}
	};
	$.snakerflow.config.tools.states.task.props.assigneeDisplay.editor = function(){return new _f("assignee")};
	$.snakerflow.config.tools.states.task.props.form.editor = function(){return new _f("form")};
});

function _snakerflow_dialog(arg) {
	$.layer({
	    type: 1,
	    shadeClose: true,
	    shade: [0.5, '#000'],
	    offset: ['20px','20px'],
	    area: ['500px', '500px'],
	    title: false,
	    border: [0],
	    page: {url : home_base + '/dialog/' + arg}
	});
};
