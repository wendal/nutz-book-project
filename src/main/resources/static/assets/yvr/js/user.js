
	$(function(){
		$("#user_index_nickname").editInPlace({
    		url: ctxPath+"/yvr/u/profile/update/nickname",
    		textarea_rows : 1,
    		text_size:20,
			g_over: "none",
			bg_out: "none",
    		field_type: "textarea",
			value_required: "true",
			success: function(data){
				$("#user_index_nickname").html(data);
			},
			error: function(e){
				if (console)
					console.log(e);
				layer.alert("修改昵称失败了");
			}
		});
		if (typeof(WebUploader) == "undefined")
			return;
		var uploader = WebUploader.create({
            swf: ctxPath+'/libs/webuploader/Uploader.swf',
            server: ctxPath+'/yvr/u/profile/update/avatar',
            pick: "#user_index_avatar",
            //paste: document.body,
            //dnd: this.$upload[0],
            auto: true,
            fileSingleSizeLimit: 256 * 1024,
            //sendAsBinary: true,
            // 只允许选择图片文件
            accept: {
                title: 'Images',
                extensions: 'gif,jpg,jpeg,png',
                mimeTypes: 'image/*'
            }
        });
        uploader.on('uploadSuccess', function() {
    		location.reload();
		});
	});

$("#MySign").editInPlace({
    url: ctxPath+"/yvr/u/profile/update/description",
    textarea_rows : 5,
    text_size:20,
	bg_over: "none",
	bg_out: "none",
    field_type: "textarea",
	value_required: "true",
	success: function(data){
		$("#MyResume").html(data);
	},
	error: function(e){
		if (console)
			console.log(e);
		layer.alert("修改个性签名失败了");
	}
});


	function resetToken() {
		$.ajax({
			url : ctxPath+"/yvr/u/me/reset/token",
			method : "POST",
			success : function() {
				window.location.reload();
			}
		});
	}