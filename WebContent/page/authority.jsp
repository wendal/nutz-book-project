<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="row">
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
	<div>
		<div id="user_role">
			<form action="#" id="user_query">
				用户过滤<input name="query">
				页数<input name="pageNumber" type="number" value="1" onchange="loadUsers();">
				每页大小<input name="pageSize" type="number" value="10" onchange="loadUsers();">
			</form>
			<p id="user_count"></p>
			<div id="user_list"></div>
		</div>
		<div id="roles">
			<form action="#" id="role_query">
				角色过滤<input name="query">
				页数<input name="pageNumber" type="number" value="1" onchange="loadRoles();">
				每页大小<input name="pageSize" type="number" value="10" onchange="loadRoles();">
			</form>
			<p id="role_count"></p>
			<div>
				<table>
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
		<div id="permissions">
			<form action="#" id="permission_query">
				权限过滤<input name="query">
				页数<input name="pageNumber" type="number" value="1" onchange="loadPermissions();">
				每页大小<input name="pageSize" type="number" value="10" onchange="loadPermissions();">
			</form>
			<p id="permission_count"></p>
			
			<div>
				<table>
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
	<div style="display: none;" id="user_modify">
		
	</div>
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
</div>
<script type="text/javascript">
	_r = {};
	function myInit(args) {
		$("#_user_role").click();
		loadUsers();
		loadRoles();
		loadPermissions();
	};
	function showTab(tab) {
		$(".ripple-button").each(function(_, z) {
			z = $(z);
			tabId = z.attr("id");
			if (tabId == undefined)
				return;
			tabId = tabId.substring(1)
			TAB = $("#" + tabId);
			if (tabId == tab) {
				TAB.show();
			}
			else {
				TAB.hide();
			}
		});
	};
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
					var tmp = "\n<p>" + user.id + " " + user.name
							  + " <button onclick='user_role_update(" + user.id +");'>修改</button> "
							  + "</p>";
					list_html += tmp;
				}
				$("#user_list").html(list_html);
				_r._user_roles = data;
				//console.log(window._user_roles);
			}
		});
	};
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
					for ( var p in role.permissions) {
						if (p.alias)
							pstr += p.alias;
						else
							pstr += p.name;
						pstr += " ";
					}
					var tmp = "\n<tr>"
							  +"<td>" + role.id + "</td>"
							  +"<td>" + role.name + "</td>"
							  +"<td>" + (role.alias ? role.alias : "" ) + "</td>"
							  +"<td></td>"
							  +"<td>" + pstr + "</td>"
							  +"<td> "
							  +"<button onclick='role_update(" + role.id +");'>修改</button> "
							  +"<button onclick='role_delete(" + role.id +");'>删除</button> "
							  +"</td>"
							  + "</tr>";
					list_html += tmp;
				}
				$("#role_list").html(list_html);
				_r._roles = data;
				//console.log(window._user_roles);
			}
		});
	};
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
						  +"<button onclick='permission_update(" + permission.id +");'>修改</button> "
						  +"<button onclick='permission_delete(" + permission.id +");'>删除</button> "
						  +"</td>"
						  + "</tr>";
					list_html += tmp;
				}
				$("#permission_list").html(list_html);
				_r._permissions = data;
				//console.log(window._user_roles);
			}
		});
	};
	
	// 各种操作哦哦哦
	
	function user_role_update(user_id) {
		alert("暂无实现");
	};
	function role_update(role_id) {
		
	};
	function role_delete(role_id) {
		$.ajax({
			url : base + "/admin/authority/role/delete",
			type : "POST",
			data : $.toJSON({id:role_id})
		});
		loadRoles();
	};
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
		};
	};
	function permission_update_submit() {
		var permission_id = $("#permission_modify_id").val();
		var p = {id:permission_id};
		p["alias"] = $("#permission_modify_alias").val();
		p["description"] = $("#permission_modify_description").val();
		$.ajax({
			url : home_base + "/admin/authority/permission/update",
			type : "POST",
			data : JSON.stringify(p),
			success : function() {
				loadPermissions();
				$("#permission_modify").hide();
			}
		});
	};
	function permission_delete(permission_id) {
		$.ajax({
			url : home_base + "/admin/authority/permission/delete",
			type : "POST",
			data : JSON.stringify({id:role_id}),
			success : function() {
				loadPermissions();
				$("#permission_modify").hide();
			}
		});
		loadPermissions();
	};
	
</script>