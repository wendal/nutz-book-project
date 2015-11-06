<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- 用bootstrap先应付一下 -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>


<div class="row">
	<!-- 下面的ul是3个Table -->
	<ul class="md-tab-group">
		<li>
			<div class="ripple-button" id="_user_role" onclick="showTab('user_role')">用户权限一览</div>
		</li>
		<li>
			<div class="ripple-button" id="_roles" onclick="showTab('roles')">角色一览</div>
		</li>
		<li>
			<div class="ripple-button" id="_permissions" onclick="showTab('permissions')">权限一览</div>
		</li>
		<div class="tab-bottom"></div>
	</ul>
</div>
	<div>
		
		<!-- 用户列表 -->
		<div id="user_role">
			<h2>用户管理</h2>
			<p id="user_count"></p>
			<form action="#" id="user_query">
				用户过滤<input name="query">
				页数<input name="pageNumber" type="number" value="1" onchange="loadUsers();">
				每页大小<input name="pageSize" type="number" value="10" onchange="loadUsers();">
			</form>
			
			<div class="panel panel-default">
				<div class="panel-heading">用户一览</div>
				<table class="table">
					<thead>
						<tr>
							<th>#</th>
							<th>名称</th>
							<th>别称</th>
							<th>角色</th>
							<th>特许权限</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody id="user_list"></tbody>
				</table>
			</div>
		</div>



		<!-- 角色列表 -->
		<div id="roles">
			<h2>角色管理</h2>
			<p id="role_count"></p>
			<form action="#" id="role_query">
				角色过滤<input name="query">
				页数<input name="pageNumber" type="number" value="1" onchange="loadRoles();">
				每页大小<input name="pageSize" type="number" value="10" onchange="loadRoles();">
			</form>
			<div>
			
			</div>
			<button onclick="role_add();" class="btn btn-default">新增角色</button>
			<div class="panel panel-default">
				<div class="panel-heading">角色一览</div>
				<table class="table">
					<thead>
						<tr>
							<th>#</th>
							<th>角色</th>
							<th>别称</th>
							<th>描述</th>
							<th>权限</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody id="role_list"></tbody>
				</table>
			</div>
		</div>
		<!-- 权限列表 -->
		<div id="permissions">
			<h2>权限管理</h2>
			<p id="permission_count"></p>
			<form action="#" id="permission_query">
				权限过滤<input name="query">
				页数<input name="pageNumber" type="number" value="1" onchange="loadPermissions();">
				每页大小<input name="pageSize" type="number" value="10" onchange="loadPermissions();">
			</form>
			<button onclick="permission_add();" class="btn btn-default">新增</button>
			
			<div class="panel panel-default">
				<div class="panel-heading">权限一览</div>
				<table class="table">
					<thead>
						<tr>
							<th>#</th>
							<th>权限</th>
							<th>别称</th>
							<th>描述</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody id="permission_list"></tbody>
				</table>
			</div>
		</div>
	</div>
	
	<!-- 各种默认隐藏的div -->
	<!-- 修改用户的特许权限 -->
	<div style="display: none;" id="user_permission_modify">
		<p>
			<input id="user_permission_modify_id" value="" hidden="true">
			<input id="user_permission_modify_name" value="" hidden="true">
		</p>
		<form action="#">
			<div id="user_permissions_div"></div>
		</form>
		<button onclick="user_permission_modify_submit();">提交</button>
	</div>
	<!-- 修改用户所属的角色 -->
	<div style="display: none;" id="user_role_modify">
		<p>
			<input id="user_role_modify_id" value="" hidden="true">
			<input id="user_role_modify_name" value="" hidden="true">
		</p>
		<form action="#">
			<div id="user_roles_div"></div>
		</form>
		<button onclick="user_role_modify_submit();">提交</button>
	</div>
	<!-- 修改角色所拥有的权限 -->
	<div style="display: none;" id="role_permission_modify">
		<p>
			<input id="role_permission_modify_id" value="" hidden="true">
			<input id="role_permission_modify_name" value="" hidden="true">
		</p>
		<form action="#">
			<div id="role_permissions_div"></div>
		</form>
		<button onclick="role_permission_modify_submit();">提交</button>
	</div>
	<!-- 修改角色的一般属性,如别称,描述 -->
	<div style="display: none;" id="role_modify">
		<div style="display: none;">
			<input name="role_modify_id" id="role_modify_id">
		</div>
		<div>
            <div class="md-text-field has-float-label">
                <label>${msg['authority.role.alias']}</label>
                <input type="text" id="role_modify_alias" name="alias" nm="${msg['authority.role.alias']}" value="">
                <hr class="underline">
                <hr class="underline-focus">
                <div class="err-tip">别名不能为空</div>
            </div>
        </div>
        <div>
            <div class="md-text-field has-float-label">
                <label>${msg['authority.role.description']}</label>
                <input type="text" id="role_modify_description" name="description" nm="${msg['authority.role.description']}" value="">
                <hr class="underline">
                <hr class="underline-focus">
                <div class="err-tip">描述不能为空</div>
            </div>
        </div>
        <div>
            <button class="md-button raised-button is-primary" type="submit" onclick="role_update_submit();">
                <span class="md-button-label">修改</span>
            </button>
            <button class="md-button raised-button is-primary" type="submit" onclick="$('#role_modify').hide();">
                <span class="md-button-label">取消</span>
            </button>
        </div>
	</div>
	<!-- 修改权限的一般属性 -->
	<div style="display: none;" id="permission_modify">
		<div style="display: none;">
			<input name="permission_modify_id" id="permission_modify_id">
		</div>
		<div>
            <div class="md-text-field has-float-label">
                <label>${msg['authority.permission.alias']}</label>
                <input type="text" id="permission_modify_alias" name="alias" nm="${msg['authority.permission.alias']}" value="">
                <hr class="underline">
                <hr class="underline-focus">
                <div class="err-tip">别名不能为空</div>
            </div>
        </div>
        <div>
            <div class="md-text-field has-float-label">
                <label>${msg['authority.permission.description']}</label>
                <input type="text" id="permission_modify_description" name="description" nm="${msg['authority.permission.description']}" value="">
                <hr class="underline">
                <hr class="underline-focus">
                <div class="err-tip">描述不能为空</div>
            </div>
        </div>
        <div>
            <button class="md-button raised-button is-primary" type="submit" onclick="permission_update_submit();">
                <span class="md-button-label">修改</span>
            </button>
            <button class="md-button raised-button is-primary" type="submit" onclick="$('#permission_modify').hide();">
                <span class="md-button-label">取消</span>
            </button>
        </div>
	</div>
	
<script type="text/javascript">
	_r = {};
	/*页面片段的初始化方法*/
	function myInit(args) {
		// 载入用户列表
		loadUsers();
		// 载入角色列表
		loadRoles();
		// 载入权限列表
		loadPermissions();
		// 默认显示用户列表的Tab
		$("#_user_role").click();
	}
	/*切换Tab的函数*/
	function showTab(tab) {
		$(".ripple-button").each(function(_, z) {
			z = $(z);
			tabId = z.attr("id");
			if (tabId == undefined)
				return;
			tabId = tabId.substring(1);
			TAB = $("#" + tabId);
			if (tabId == tab) {
				TAB.show();
			}
			else {
				TAB.hide();
			}
		});
	}
	/*载入用户列表*/
	function loadUsers() {
		$.ajax({
			url : home_base + "/admin/authority/users",
			data : $("#user_query").serialize(),
			dataType : "json",
			type : "POST",
			success : function(re) {
				if (!re.ok) {
					alert(re.msg);
					return;
				}
				data = re.data;
				//console.log(data);
				$("#user_count").html("共"+data.pager.recordCount+"个用户, 总计"+data.pager.pageCount+"页");
				var list_html = "";
				//console.log(data.list);
				for (var i=0;i<data.list.length;i++) {
					var user = data.list[i];
					console.log(user);
					var pstr = "";
					for (var j=0;j<user.permissions.length;j++) {
						var p = user.permissions[j];
						if (p.alias)
							pstr += p.alias;
						else
							pstr += p.name;
						pstr += " ";
						//console.log(p);
						if (j > 4 && user.permissions.length > 4) {
							pstr += " 等" + user.permissions.length + "种权限";
							break;
						}
					}
					var rstr = "";
					for (var j=0;j<user.roles.length;j++) {
						var r = user.roles[j];
						if (r.alias)
							rstr += r.alias;
						else
							rstr += r.name;
						rstr += " ";
						//console.log(p);
						if (j > 4 && user.roles.length > 4) {
							rstr += " 等" + user.roles.length + "种角色";
							break;
						}
					}
					var tmp = "\n<tr>"
						  	+"<td>" + user.id + "</td>"
						  	+"<td>" + user.name + "</td>"
						  	+"<td>" + (user.alias ? user.alias : "" ) + "</td>"
						  	+"<td>" + rstr + "</td>"
						  	+"<td>" + pstr + "</td>"
						  	+"<td> "
							+ " <button onclick='user_role_update(" + user.id +");' class='btn btn-default'>修改角色</button> "
							+ " <button onclick='user_permission_update(" + user.id +");' class='btn btn-default'>修改特许权限</button> "
							+"</td>"
							+ "</tr>";
					list_html += tmp;
				}
				$("#user_list").html(list_html);
				_r._user_roles = data;
				//console.log(window._user_roles);
			}
		});
	}
	/*载入角色列表*/
	function loadRoles() {
		$.ajax({
			url : home_base + "/admin/authority/roles",
			data : $("#role_query").serialize(),
			dataType : "json",
			type : "POST",
			success : function(re) {
				if (!re.ok) {
					alert(re.msg);
					return;
				}
				data = re.data;
				//console.log(data);
				$("#role_count").html("共"+data.pager.recordCount+"个角色, 总计"+data.pager.pageCount+"页");
				var list_html = "";
				//console.log(data.list);
				for (var i=0;i<data.list.length;i++) {
					var role = data.list[i];
					var pstr = "";
					for (var j=0;j<role.permissions.length;j++) {
						var p = role.permissions[j];
						if (p.alias)
							pstr += p.alias;
						else
							pstr += p.name;
						pstr += " ";
						//console.log(p);
						if (j > 4 && role.permissions.length > 4) {
							pstr += " 等" + role.permissions.length + "种权限";
							break;
						}
					}
					var tmp = "\n<tr>"
							  +"<td>" + role.id + "</td>"
							  +"<td>" + role.name + "</td>"
							  +"<td>" + (role.alias ? role.alias : "" ) + "</td>"
							  +"<td></td>"
							  +"<td>" + pstr + "</td>"
							  +"<td> "
							  +"<button onclick='role_update(" + role.id +");' class='btn btn-default'>修改描述</button> "
							  +"<button onclick='role_permission_update(" + role.id +");' class='btn btn-default'>修改权限</button> "
							  +"<button onclick='role_delete(" + role.id +");' class='btn btn-default'>删除</button> "
							  +"</td>"
							  + "</tr>";
					list_html += tmp;
				}
				$("#role_list").html(list_html);
				_r._roles = data;
				//console.log(window._user_roles);
			}
		});
	}
	/*载入权限列表*/
	function loadPermissions() {
		$.ajax({
			url : home_base + "/admin/authority/permissions",
			data : $("#permission_query").serialize(),
			dataType : "json",
			type : "POST",
			success : function(re) {
				if (!re.ok) {
					alert(re.msg);
					return;
				}
				data = re.data;
				//console.log(data);
				$("#permission_count").html("共"+data.pager.recordCount+"个权限, 总计"+data.pager.pageCount+"页");
				var list_html = "";
				//console.log(data.list);
				for (var i=0;i<data.list.length;i++) {
					var permission = data.list[i];
					var tmp = "\n<tr>"
						  +"<td>" + permission.id + "</td>"
						  +"<td>" + permission.name + "</td>"
						  +"<td>" + (permission.alias ? permission.alias : "" ) + "</td>"
						  +"<td>" + (permission.description? permission.description : "") + "</td>"
						  +"<td> "
						  +"<button onclick='permission_update(" + permission.id +");' class='btn btn-default'>修改</button> "
						  +"<button onclick='permission_delete(" + permission.id +");' class='btn btn-default'>删除</button> "
						  +"</td>"
						  + "</tr>";
					list_html += tmp;
				}
				$("#permission_list").html(list_html);
				_r._permissions = data;
				//console.log(window._user_roles);
			}
		});
	}
	// 各种操作哦哦哦
	
	/*更新用户特许权限*/
	function user_permission_update(user_id) {
		$.ajax({
			url : home_base + "/admin/authority/user/fetch/permission",
			dataType : "json",
			data : {id:user_id},
			success : function (re) {
				if (re && re.ok) {
					console.log(re.data);
					var html = "";
					var ps = re.data.permissions;
					for (var i = 0; i < ps.length; i++) {
						var p = ps[i];
						var flag = false;
						for (var j = 0; j < re.data.user.permissions.length; j++) {
							if (re.data.user.permissions[j].id == p.id) {
								flag = true;
								break;
							}
						}
						if (p.alias) {
							html += p.alias;
						} else {
							html += p.name;
						}
						if (flag) {
							html += "<input type='checkbox' t='checkbox_user_permission' pid='" + p.id + "' checked='true'>\n"
						} else {
							html += "<input type='checkbox' t='checkbox_user_permission' pid='" + p.id + "'>\n"
						}
					}
					$("#user_permission_modify_id").val(""+user_id);
					$("#user_permission_modify_name").val(""+re.data.user.name);
					$("#user_permissions_div").html(html);
					$("#user_permission_modify").show();
				}
			}
		});
	}
	/*将用户的特许权限提交到服务器*/
	function user_permission_modify_submit() {
		var user_id = $("#user_permission_modify_id").val();
		var ps = $("input[t='checkbox_user_permission']:checked");
		console.log(ps);
		console.log(user_id);
		var pids = [];
		ps.each(function(i, p_input) {
			pids.push($(p_input).attr("pid"));
		});
		$.ajax({
			url : home_base + "/admin/authority/user/update",
			type : "POST",
			data : JSON.stringify({"user":{id:user_id}, "permissions":pids}),
			success : function() {
				loadUsers();
				$("#user_permission_modify").hide();
			}
		});
	}
	function user_role_update(user_id) {
		$.ajax({
			url : home_base + "/admin/authority/user/fetch/role",
			dataType : "json",
			data : {id:user_id},
			success : function (re) {
				if (re && re.ok) {
					console.log(re.data);
					var html = "";
					var ps = re.data.roles;
					for (var i = 0; i < ps.length; i++) {
						var p = ps[i];
						var flag = false;
						for (var j = 0; j < re.data.user.roles.length; j++) {
							if (re.data.user.roles[j].id == p.id) {
								flag = true;
								break;
							}
						}
						if (p.alias) {
							html += p.alias;
						} else {
							html += p.name;
						}
						if (flag) {
							html += "<input type='checkbox' t='checkbox_user_role' pid='" + p.id + "' checked='true'>\n"
						} else {
							html += "<input type='checkbox' t='checkbox_user_role' pid='" + p.id + "'>\n"
						}
					}
					$("#user_role_modify_id").val(""+user_id);
					$("#user_role_modify_name").val(""+re.data.user.name);
					$("#user_roles_div").html(html);
					$("#user_role_modify").show();
				}
			}
		});
	}
	function user_role_modify_submit() {
		var user_id = $("#user_role_modify_id").val();
		var ps = $("input[t='checkbox_user_role']:checked");
		console.log(ps);
		console.log(user_id);
		var pids = [];
		ps.each(function(i, p_input) {
			pids.push($(p_input).attr("pid"));
		});
		$.ajax({
			url : home_base + "/admin/authority/user/update",
			type : "POST",
			data : JSON.stringify({"user":{id:user_id}, "roles":pids}),
			success : function() {
				loadUsers();
				$("#user_role_modify").hide();
			}
		});
	}
	//----------------------------------------------------
	// 角色相关的操作
	/*更新角色的一般属性,显示所需要的输入框*/
	function role_update(role_id) {
		for (var i=0;i<_r._roles.list.length;i++) {
			var role = _r._roles.list[i];
			if (role.id == role_id) {
				$("#role_modify_id").attr("value", role_id);
				$("#role_modify_alias").attr("value", role.alias);
				$("#role_modify_description").attr("value", role.description);
				$("#role_modify").show();
				return;
			}
		}
	}
	/*将角色的一般属性的修改发送到服务器进行修改*/
	function role_update_submit() {
		var role_id = $("#role_modify_id").val();
		var p = {id:role_id};
		p["alias"] = $("#role_modify_alias").val();
		p["description"] = $("#role_modify_description").val();
		console.log(p);
		$.ajax({
			url : home_base + "/admin/authority/role/update",
			type : "POST",
			data : JSON.stringify({"role":p}),
			success : function() {
				loadRoles();
				$("#role_modify").hide();
			}
			// TODO 处理异常
		});
	}
	/*删除一个角色*/
	function role_delete(role_id) {
		// TODO 提示用户确认
		$.ajax({
			url : home_base + "/admin/authority/role/delete",
			type : "POST",
			data : JSON.stringify({id:role_id})
		});
		loadRoles();
	}
	/*新增角色*/
	function role_add() {
		var role_name = prompt("请输入新角色的名称,仅限英文字母,长度3到10个字符");
		var re = /[a-zA-Z]{3,10}/;  
		if (role_name && re.exec(role_name)) {
			$.ajax({
				url : home_base + "/admin/authority/role/add",
				type : "POST",
				data : JSON.stringify({name:role_name}),
				success : function () {
					loadRoles();
				}
			});
		}
	}
	/*更新角色所拥有的权限列表,这个方法用于显示多选框*/
	function role_permission_update(role_id) {
		$.ajax({
			url : home_base + "/admin/authority/role/fetch",
			dataType : "json",
			data : {id:role_id},
			success : function (re) {
				if (re && re.ok) {
					console.log(re.data);
					var html = "";
					var ps = re.data.permissions;
					for (var i = 0; i < ps.length; i++) {
						var p = ps[i];
						var flag = false;
						for (var j = 0; j < re.data.role.permissions.length; j++) {
							if (re.data.role.permissions[j].id == p.id) {
								flag = true;
								break;
							}
						}
						if (p.alias) {
							html += p.alias;
						} else {
							html += p.name;
						}
						if (flag) {
							html += "<input type='checkbox' t='checkbox_role_permission' pid='" + p.id + "' checked='true'>\n"
						} else {
							html += "<input type='checkbox' t='checkbox_role_permission' pid='" + p.id + "'>\n"
						}
					}
					$("#role_permission_modify_id").val(""+role_id);
					$("#role_permission_modify_name").val(""+re.data.role.name);
					$("#role_permissions_div").html(html);
					$("#role_permission_modify").show();
				}
			}
		});
	}
	/*将角色的权限列表提交到服务器去*/
	function role_permission_modify_submit() {
		var role_id = $("#role_permission_modify_id").val();
		var ps = $("input[t='checkbox_role_permission']:checked");
		console.log(ps);
		console.log(role_id);
		var pids = [];
		ps.each(function(i, p_input) {
			pids.push($(p_input).attr("pid"));
		});
		$.ajax({
			url : home_base + "/admin/authority/role/update",
			type : "POST",
			data : JSON.stringify({"role":{id:role_id}, "permissions":pids}),
			success : function() {
				loadRoles();
				$("#role_permission_modify").hide();
			}
		});
	}
	/*更新权限的一般信息,显示输入框*/
	function permission_update(permission_id) {
		for (var i=0;i<_r._permissions.list.length;i++) {
			var permission = _r._permissions.list[i];
			if (permission.id == permission_id) {
				$("#permission_modify_id").attr("value", permission_id);
				$("#permission_modify_alias").attr("value", permission.alias);
				$("#permission_modify_description").attr("value", permission.description);
				$("#permission_modify").show();
				return;
			}
		}
	}
	/*把权限的一般信息的修改提交到服务器*/
	function permission_update_submit() {
		var permission_id = $("#permission_modify_id").val();
		var p = {id:permission_id};
		p["alias"] = $("#permission_modify_alias").val();
		p["description"] = $("#permission_modify_description").val();
		console.log(p);
		$.ajax({
			url : home_base + "/admin/authority/permission/update",
			type : "POST",
			data : JSON.stringify(p),
			success : function() {
				loadPermissions();
				$("#permission_modify").hide();
			}
		});
	}
	/*删除一个权限*/
	function permission_delete(permission_id) {
		$.ajax({
			url : home_base + "/admin/authority/permission/delete",
			type : "POST",
			data : JSON.stringify({id:permission_id}),
			success : function() {
				loadPermissions();
				$("#permission_modify").hide();
			}
		});
		loadPermissions();
	}
	/*新增一个权限*/
	function permission_add() {
		var permission_name = prompt("请输入新角色的名词,仅限英文字母/冒号/米号,长度3到30个字符");
		var re = /[a-zA-Z\:\*]{3,10}/;  
		if (permission_name && re.exec(permission_name)) {
			$.ajax({
				url : home_base + "/admin/authority/permission/add",
				type : "POST",
				data : JSON.stringify({name:permission_name}),
				success : function () {
					loadPermissions();
				}
			});
		}
	}

</script>