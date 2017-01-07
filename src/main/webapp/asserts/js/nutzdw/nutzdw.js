var nutzdw = new Vue({
	el : '#nutzdw',
	data : {
		list : [],
		_match : ""
	},
	methods : {
		doReload : function() {
			this.$http.get(base + "/nutzdw/list", {}).then(
					function(resp) {
						// console.log(resp.ok);
						if (resp.ok) {
							var re = resp.json();
							//console.info(re.data);
							for (var index in re.data) {
								var tmp = re.data[index].name;
								if (tmp.indexOf("/") > 0)
									re.data[index].name = tmp.substring(tmp.lastIndexOf("/") + 1);
								re.data[index].url = dw_domain + tmp;
							}
							this.list = re.data;
						}
					});
		},
		addRecord : function() {
		},
		updateRecord : function(id, re) {

		},
		deleteRecord : function(id) {

		},
		upload : function() {

		}
	},
	created : function() {
		this.doReload();
	}
});
if (Qiniu) {
	var uploader = Qiniu
			.uploader({
				runtimes : 'html5,flash,html4', // 上传模式，依次退化
				browse_button : 'pickfiles', // 上传选择的点选按钮，必需
				uptoken_url : base + '/nutzdw/uptoken', // Ajax请求uptoken的Url，强烈建议设置（服务端提供）
				get_new_uptoken : true, // 设置上传文件的时候是否每次都重新获取新的uptoken
				max_file_size : '128mb', // 最大文件体积限制
				// flash_swf_url: 'path/of/plupload/Moxie.swf', //引入flash，相对路径
				max_retries : 3, // 上传失败最大重试次数
				// dragdrop: true, // 开启可拖曳上传
				// drop_element: 'container', // 拖曳上传区域元素的ID，拖曳文件或文件夹后可触发上传
				chunk_size : '4mb', // 分块上传时，每块的体积
				auto_start : true, // 选择文件后自动上传，若关闭需要自己绑定事件触发上传
				unique_names : false,
				domain : dw_domain,
				init : {
					'FileUploaded' : function(up, file, info) {
						var progress = new FileProgress(file,
								'fsUploadProgress');
						progress.setComplete(up, info);

						info = JSON.parse(info);
						console.log(info);
						info["name"] = info["key"];
						info["md5"] = info["hash"];
						$.ajax({
							url : base + "/nutzdw/add",
							data : info,
							type : "POST",
							success : function() {
								nutzdw.doReload();
							}
						});
					},
					'FilesAdded' : function(up, files) {
						$('table').show();
						$('#success').hide();
						plupload.each(files, function(file) {
							var progress = new FileProgress(file,'fsUploadProgress');
							progress.setStatus("等待...");
							progress.bindUploadCancel(up);
						});
					},
					'BeforeUpload' : function(up, file) {
						var progress = new FileProgress(file,'fsUploadProgress');
						var chunk_size = plupload.parseSize(this.getOption('chunk_size'));
						if (up.runtime === 'html5' && chunk_size) {
							progress.setChunkProgess(chunk_size);
						}
					},
					'UploadProgress' : function(up, file) {
						var progress = new FileProgress(file,'fsUploadProgress');
						var chunk_size = plupload.parseSize(this.getOption('chunk_size'));
						progress.setProgress(file.percent + "%", file.speed,chunk_size);
					},
					'UploadComplete' : function() {
						$('#success').show();
					},
					'Error' : function(up, err, errTip) {
						$('table').show();
						var progress = new FileProgress(err.file, 'fsUploadProgress');
						progress.setError();
						progress.setStatus(errTip);
					},
					'Key' : function(up, file) {
						var key = dw_prefix + file.name;
						return key
					}
				}
			});

}