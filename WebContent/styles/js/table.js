function jumpPage(pageNo) {
	$("#pageNo").val(pageNo);
	$("#mainForm").submit();
}

function sort(orderBy, defaultOrder) {
	if ($("#orderBy").val() == orderBy) {
		if ($("#order").val() == "") {
			$("#order").val(defaultOrder);
		}
		else if ($("#order").val() == "desc") {
			$("#order").val("asc");
		}
		else if ($("#order").val() == "asc") {
			$("#order").val("desc");
		}
	}
	else {
		$("#orderBy").val(orderBy);
		$("#order").val(defaultOrder);
	}

	$("#mainForm").submit();
}

function search() {
	$("#order").val("");
	$("#orderBy").val("");
	$("#pageNo").val("1");
	$("#mainForm").submit();
}

function addNew(url) {
	window.location.href=url;
}

function gotoPage() {
 	var str = document.all.jumppage.value;
    var i;
    for (i = 0; i < str.length; i++) {
       if ((str.charAt(i)<'0') || (str.charAt(i)>'9')) {
          alert("请您输入正确的页号");
          return false;
       }
    }
	str = parseInt(str);
	var lastpage = parseInt(document.all.lastpage.value);
	if( str > lastpage ) {
		str = lastpage ;
	}
	$("#pageNo").val(str);
	$("#mainForm").submit();
}

function exportExcel(url) {
	window.location.href=url;
}

function confirmDel() {
	if(confirm("请确定是否删除?")) { 
		return true;
	} else {
		return false;
	} 
}

function bringback(id, name) {
	window.parent.callbackProcess(id,name);
}
