<#import "../layout/main.ftl" as layout>
<@layout.admin2Layout title="后台管理系统" admin_menu_level_1="${msg['admin_menu.openvpn.level_one']}"  admin_menu_level_2="OpenVPN管理" >



<div class="widget">
    <div class="widget-head">
        <div class="pull-left">Data Tables</div>
        <div class="widget-icons pull-right">
            <a href="#" class="wminimize"><i class="fa fa-chevron-up"></i></a>
            <a href="#" class="wclose"><i class="fa fa-times"></i></a>
        </div>
        <div class="clearfix"></div>
    </div>
    <div class="widget-content">
        <div class="padd">

            <!-- Table Page -->
            <div class="page-tables">
                <!-- Table -->
                <div class="table-responsive">
                    <table cellpadding="0" cellspacing="0" border="0" id="user_list_table" width="100%">
                        <thead>
                        <tr>
                            <th>id</th>
                            <th>ip地址</th>
                            <th>macid</th>
                            <th>平台</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                    <div class="clearfix"></div>
                </div>
            </div>
        </div>


    </div>
    <div class="widget-foot">
        <!-- Footer goes here -->
    </div>
</div>


<div class="widget">
	<div class="widget-head">
        <div class="pull-left">Data Tables</div>
        <div class="widget-icons pull-right">
            <a href="#" class="wminimize"><i class="fa fa-chevron-up"></i></a>
            <a href="#" class="wclose"><i class="fa fa-times"></i></a>
        </div>
        <div class="clearfix"></div>
    </div>
    <div class="widget-content">
        <div class="padd">
			<form action="${base}/openvpn/upload" enctype="multipart/form-data" method="POST">
				<input type="file" name="file"> <input type="submit" value="上传">
			</form>
		</div>
	</div>
    <div class="widget-foot">
        <!-- Footer goes here -->
    </div>
	
</div>

<script language="JavaScript">
    $(function () {

        var table = $("#user_list_table").DataTable({
            "processing": true,
            "serverSide" : true,
            "ajax" : {
                "url" : "${base}/admin/datatable/query/openvpnclient",
                "dataSrc" : "list",
                "type" : "POST"
            },
            "columns" : [
                {"data":"id"},
                {"data":"ip"},
                {"data":"macid"},
                {"data":"platform"},
                {
                	"data" : "status",
                	"render" : function(data) {
                		return data == 0 ? "未使用" : "已占用";
                	}
                },
                {
                    "data" : "userId",
                    "render" : function(data) {
                        return "<button>"+"修改"+"</button>";
                    }
                }
            ],
            "language": {
                search: "搜索"
            }
        });
        $("#user_list_table tbody").on("click", "tr", function () {
            var data = table.row( this ).data();
            if (console)
                console.warn(data);
            for (var prop in data) {
                var t = $("#t_"+prop);
                if (t) {
                    t.val(data[prop] == null ? "" : data[prop]);
                }
            }
            $("#t_modify_div").show();
        });
    });
</script>


</@layout.admin2Layout>