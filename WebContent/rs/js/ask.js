var _emd;

var _pager = {page:1};

$(function() {
	if (topicId != "") {
		showTopic();
		loadReplies();
	} else {
		reloadTopics();
	}
	var editormd_open = null;
	$("#ask_link").click(function() {

		if (editormd_open) {
			editormd_open = 0;
			$("#ask_md_div").hide();
			return;
		} else {
			$("#ask_md_div").show();
		}
		var _emd = editormd("test-editormd", {
			width : "90%",
			height : 640,
			autoHeight : true,
			path : home_base + "/rs/editormd/lib/",
			emoji : true,
			imageUpload : true,
			imageFormats : [ "jpg", "jpeg", "gif", "png", "bmp", "webp" ],
			imageUploadURL : home_base + "/ask/image/upload",
			onload : function() {
				console.log('hi,你好,在找源码还是在debug?');
			},
			toolbarIcons : function() {
				return [
		            "link", "image", "code-block", "emoji", "|",
		            "watch", "preview", "fullscreen", "clear", "|",
		            "help"
		        ]
			},
		});
		editormd_open = 1;
	});
	$("#ask_submit_button").click(submit_topic);
	
	$("#cs_qr_link").click(function(){
		var tmp = "<img src='" +home_base+ "/cs/qr?url=";
		tmp += encodeURIComponent(window.location.href) +"'>";
    	$.layer({title:"跨屏二维码有效期2分钟", type:1, time:100, page:{html:tmp}, area : ['256px', '291px']}); // 256+35 = 291
	});
	
	$("#topic_reply_button").click(function() {
		var content = $("#topic_reply_body").val();
		if (!content || content == "") {
			alert("内容不能为空");
			return;
		}
		$.ajax({
			url : home_base + "/ask/topic/" + topicId + "/reply",
			type : "POST",
			data : {content:content},
			dataType : "json",
			success : function(re) {
				if (re && re.ok) {
					window.location.reload();
				} else {
					alert(re.msg);
				}
			}
		});
	});
});

function md2html(_id) {
	var _tmp = editormd.markdownToHTML(_id, {
		emoji           : true,
        //taskList        : true,
        //tex             : true,  // 默认不解析
        //flowChart       : true,  // 默认不解析
        //sequenceDiagram : true,  // 默认不解析
	});
};

function reloadTopics() {
	$.ajax({
		url : home_base + "/ask/topics?pageNumber=" + _pager.page,
		dataType : "json",
		success : function(re) {
			if (re && re.ok) {
				console.log(re.data);
				tpl = document.getElementById("ask_table_tpl").innerHTML;
				var render = laytpl(tpl).render(re.data);
				document.getElementById('topic_list').innerHTML = render;
				$("#topic_list").show();
			}
		}
	});
};

function loadReplies() {
	$.ajax({
		url : home_base + "/ask/topic/" + topicId + "/replies",
		dataType : "json",
		success : function(re) {
			if (re && re.ok) {
				console.log(re.data);
				tpl = document.getElementById("topic_reply_tpl").innerHTML;
				var render = laytpl(tpl).render({list:re.data});
				console.log(render);
				$('#topic_replies').html(render);
				for(var i = 0, len = re.data.length; i < len; i++){
					md2html("reply_md_"+i);
				}
				
				$("#topic_replies").show();
			}
		}
	});
};

function submit_topic() {
	var data = {};
	// 先得到标题
	var title = $("#new_topic_title").val();
	if (!title || title=="") {
		alert("标题不能为空啊");
		return;
	}
	// 然后得到内容
	var content = $("#new_topic_content").val();
	if (!content || content=="") {
		alert("大哥/大姐,起码填个RT吧!!");
		return;
	}
	// 提交
	$.ajax({
		url : home_base + "/ask/topic",
		type : "POST",
		data : {"title":title, "content":content, "tab" : "ask"},
		dataType : "json",
		succss : function(re){
			if (re && re.ok) {
				alert("搞定");
				reloadTopics();
			}
		}
	});
};

function showTopic() {
	md2html("topic_view_body");
	$("#topic_display").show();
};