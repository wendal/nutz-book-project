$(function() {
  $("#txt_button").click(function() {
    $(this).attr('disabled',true);
    var data = $("#data").val();
    if (!data)
      return;
    var url = 'api/create/txt?title=' + $("#title").val();
    if (data.split(/\n/).length == 1) {
      var reg = /(https?|ftp):\/\/[^\/?:]+(:[0-9]{1,5})?(\/?|(\/[^\/]+)*(\?[^\s"']+)?)/;
      if (reg.test(data)) {
        url = 'api/create/url';
        data = {'data': data};
      }
    }
    $.ajax({
	"url" : url,
	'data': data,
	'type' : 'post',
	'contentType' : 'text/plain',
	"dataType" : "json",
	success : function (j) {
      $("#result").html("");
      $("#progress").html("");
      if (j.ok) {
    	  if (j.url) {
    		  window.location = j.url;
    	  }
        var url = location.protocol + "//" + location.host + location.pathname + 'c/' + j.code;
        $("#result").append($("<div class='alert alert-success'><p>短地址: </p></div>").append($('<a target="_blank">' + url + '</a>').attr("href", url)));
        $("#data").val("");
        //qrcodeToggle("result", "显示短地址QR Code", "隐藏短地址QR Code");
      } else {
        $("#result").append($('<div class="alert alert-error">Oops</div>')).append($("<p></p>").text("ERROR: " + j.msg));
      }
      $("#txt_button").attr('disabled',false);
    }});;
  });

  function FileDragHover(e) {
    e.stopPropagation();
    e.preventDefault();
    var target = $(e.target);
    if (e.type == "dragover") {
      target.addClass("hover");
    } else {
      target.removeClass("hover");
    }
  }

  function FileSelectHandler(e) {
    $("#progress").html("");
    // cancel event and hover styling
    FileDragHover(e);
    var files = false;
    if(e.originalEvent.dataTransfer){
      if(e.originalEvent.dataTransfer.files.length) {
        e.preventDefault();
        e.stopPropagation();
        files = e.originalEvent.dataTransfer.files;
      }
    }
    if (!files)
      return;

    var file = files[0];
    if (file.size == 0) {
      alert("Emtry File");
      return;
    }
    if (file.size > 1024*1024*10) {
      alert("Must less than 10mb");
      return;
    }
    var o = document.getElementById("progress");
    var progress = o.appendChild(document.createElement("p"));
    progress.appendChild(document.createTextNode("upload " + file.name));

    var xhr = new XMLHttpRequest();
    xhr.upload.addEventListener("progress", function(e) {
      var pc = parseInt(100 - (e.loaded / e.total * 100));
      progress.style.backgroundPosition = pc + "% 0";
    }, false);
    xhr.onreadystatechange = function(e) {
      if (xhr.readyState == 4) {
        progress.className = (xhr.status == 200 ? "success" : "failure");
        $("#result").html("");
        if (xhr.status == 200) {
          var j = eval("(" + xhr.responseText +")");
          if (j.ok) {
            var url = location.protocol + "//" + location.host + location.pathname + 'c/' + j.code;
            $("#result").append($('<div class="alert alert-success">Success</div>')).append($("<p>共享文件地址: </p>").append($('<a target="_blank">' + url + '</a>').attr("href", url))).append($('<p id="result-qrcode" style="display: none"></p>').append($('<img/>').attr('src', 'https://chart.googleapis.com/chart?chs=72x72&cht=qr&choe=UTF-8&chl=' + encodeURIComponent(url)))).append($('<p><a href="javascript:void(0);" id="result-qrcode-str">显示短地址QR Code</a></p>'));;
            qrcodeToggle("result", "显示短地址QR Code", "隐藏短地址QR Code");
          } else {
            $("#result").append($('<div class="alert alert-error">Oops</div>')).append($("<p></p>").text("ERROR: " + j.msg));
          }
          return;
        }
      }
    };
    xhr.open("POST", "api/create/file", true);
    xhr.setRequestHeader("X-File-Name", Base64.encode(file.name));
    xhr.setRequestHeader("X-File-Size", file.size);
    xhr.send(file);
  }

  $("#data").bind("dragover", FileDragHover);
  $("#data").bind("dragleave",FileDragHover);
  $("#data").bind("drop", FileSelectHandler);
});
