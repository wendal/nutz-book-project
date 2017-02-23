package net.wendal.nutzbook.adminlte.module;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.ServerRedirectView;

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.core.service.EmailService;
import net.wendal.nutzbook.core.service.UserService;

@IocBean
@At("/adminlte")
public class AdminlteModule extends BaseModule {
    
    private static final Log log = Logs.get();
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject
    protected UserService userService;
    
    @RequiresAuthentication
    @Ok("beetl:/adminlte/index.html")
    @At({"/", "/index"})
    public void index(HttpServletRequest req){
        req.setAttribute("profile", userService.getUserProfile(Toolkit.uid(), false));
    }

    @RequiresAuthentication
    @At("/page/?/?")
    @Ok("beetl:/adminlte/${pathargs[0]}/${pathargs[1]}.html")
    public void page() {}
    
    @GET
    @At(value="/user/login")
    @Ok("beetl:/adminlte/login.html")
    public Object login() {
        if (Toolkit.uid() > 0) {
            return new ServerRedirectView("/adminlte");
        }
        return null;
    }
    
    @RequiresRoles("admin")
    @POST
    @At(value="/test/mail")
    @Ok("json")
    public Object sendTestMail(@Param("to")String to) {
        try {
            ioc.get(EmailService.class).send(to, "这是一封测试邮件 " + System.currentTimeMillis(), "没有内容");
            return ajaxOk(null);
        }
        catch (Exception e) {
            log.info("send mail fail", e);
            return ajaxFail(Lang.unwrapThrow(e).getMessage());
        }
    }
}
