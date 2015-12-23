<#import "../layout/main.ftl" as layout>
<@layout.admin2Layout title="后台管理系统" admin_menu_level_1="${msg['admin_menu.users.level_one']}"  admin_menu_level_2="用户管理" >




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
                            <th>用户名</th>
                            <th>昵称</th>
                            <th>邮箱</th>
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


<div class="widget wgreen">
    <div class="widget-head">
        <div class="pull-left">修改用户</div>
        <div class="widget-icons pull-right">
            <a href="#" class="wminimize"><i class="fa fa-chevron-up"></i></a>
            <a href="#" class="wclose"><i class="fa fa-times"></i></a>
        </div>
        <div class="clearfix"></div>
    </div>
    <div class="widget-content" style="display: none" id="t_modify_div">
        <div class="padd">
            <br/>
            <!-- Form starts.  -->
            <form class="form-horizontal" role="form">

                <div class="form-group">
                    <label class="col-lg-2 control-label">id</label>
                    <div class="col-lg-5">
                        <input type="text" class="form-control" placeholder="id" disabled="disabled" id="t_userId">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-lg-2 control-label">用户名</label>
                    <div class="col-lg-5">
                        <input type="text" class="form-control" placeholder="用户名" disabled="disabled" id="t_loginname">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-lg-2 control-label">昵称</label>
                    <div class="col-lg-5">
                        <input type="text" class="form-control" placeholder="昵称" id="t_nickname">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-lg-2 control-label">邮箱</label>
                    <div class="col-lg-5">
                        <input type="text" class="form-control" placeholder="邮箱地址" id="t_email">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-lg-2 control-label">简介</label>
                    <div class="col-lg-5">
                        <textarea class="form-control" rows="5" placeholder="简介" id="t_description"></textarea>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-lg-offset-2 col-lg-6">
                        <button type="button" class="btn btn-sm btn-default">提交</button>
                    </div>
                </div>
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
                "url" : "${base}/admin/datatable/query/userprofile",
                "dataSrc" : "list",
                "type" : "POST"
            },
            "columns" : [
                {"data":"userId"},
                {"data":"loginname"},
                {"data":"nickname"},
                {"data":"email"},
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