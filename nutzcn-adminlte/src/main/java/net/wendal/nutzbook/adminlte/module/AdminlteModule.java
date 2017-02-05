package net.wendal.nutzbook.adminlte.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/adminlte")
public class AdminlteModule {

    @Ok("beetl:/adminlte/index.html")
    @At({"/", "/index"})
    public void index(){}
    
    @At("/page/?/?")
    @Ok("beetl:/adminlte/${pathargs[0]}/${pathargs[1]}.html")
    public void page() {}
}
