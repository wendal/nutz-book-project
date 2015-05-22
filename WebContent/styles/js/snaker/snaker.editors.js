(function($){
var snakerflow = $.snakerflow;

$.extend(true, snakerflow.editors, {
	inputEditor : function(){
		var _props,_k,_div,_src,_r;
		this.init = function(props, k, div, src, r){
			_props=props; _k=k; _div=div; _src=src; _r=r;
			
			$('<input style="width:98%;"/>').val(props[_k].value).change(function(){
				props[_k].value = $(this).val();
			}).appendTo('#'+_div);
			
			$('#'+_div).data('editor', this);
		}
		this.destroy = function(){
			$('#'+_div+' input').each(function(){
				_props[_k].value = $(this).val();
			});
		}
	},
	selectEditor : function(arg){
		var _props,_k,_div,_src,_r;
		this.init = function(props, k, div, src, r){
			_props=props; _k=k; _div=div; _src=src; _r=r;

			var sle = $('<select  style="width:99%;"/>').val(props[_k].value).change(function(){
				props[_k].value = $(this).val();
			}).appendTo('#'+_div);
			
			if(typeof arg === 'string'){
				$.ajax({
				   type: "GET",
				   url: arg,
				   success: function(data){
					  var opts = eval(data);
					 if(opts && opts.length){
						for(var idx=0; idx<opts.length; idx++){
							sle.append('<option value="'+opts[idx].value+'">'+opts[idx].name+'</option>');
						}
						sle.val(_props[_k].value);
					 }
				   }
				});
			}else {
				for(var idx=0; idx<arg.length; idx++){
					sle.append('<option value="'+arg[idx].value+'">'+arg[idx].name+'</option>');
				}
				sle.val(_props[_k].value);
			}
			
			$('#'+_div).data('editor', this);
		};
		this.destroy = function(){
			$('#'+_div+' input').each(function(){
				_props[_k].value = $(this).val();
			});
		};
	},
	assigneeEditor : function(arg){
		var _props,_k,_div,_src,_r;
		this.init = function(props, k, div, src, r){
			_props=props; _k=k; _div=div; _src=src; _r=r;

			$('<input style="width:88%;" readonly="true" id="dialogEditor"/>').val(props[_k].value).appendTo('#'+_div);
			$('<input style="width:10%;" type="button" value="选择"/>').click(function(){
				//alert("选择:" + snakerflow.config.ctxPath + arg);
				var element = document.getElementById("dialogEditor");
				var l  = window.showModalDialog(snakerflow.config.ctxPath + arg," ","dialogWidth:800px;dialogHeight:540px;center:yes;scrolling:yes");
				if (l == null )
					return;
				var result = splitUsersAndAccounts(l);
				element.title = result[1];
				element.value = result[1];
				props[_k].value = result[1];
				props['assignee'].value = result[0];
			}).appendTo('#'+_div);

			$('#'+_div).data('editor', this);
		}
		this.destroy = function(){
			//
		}
	}
});

})(jQuery);