package net.wendal.nutzbook.adminlte.module;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.ServerRedirectView;

import net.wendal.nutzbook.common.util.Toolkit;

@IocBean
@At("/adminlte")
public class AdminlteModule {
    
    @RequiresAuthentication
    @Ok("beetl:/adminlte/index.html")
    @At({"/", "/index"})
    public void index(){
    }

    @RequiresAuthentication
    @At("/page/?/?")
    @Ok("beetl:/adminlte/${pathargs[0]}/${pathargs[1]}.html")
    public void page() {}
    
    @GET
    @At(value="/user/login", top=true)
    @Ok("beetl:/adminlte/login.html")
    public Object login() {
        if (Toolkit.uid() > 0) {
            return new ServerRedirectView("/adminlte");
        }
        return null;
    }
}
