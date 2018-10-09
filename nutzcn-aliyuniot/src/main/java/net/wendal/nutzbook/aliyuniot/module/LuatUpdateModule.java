package net.wendal.nutzbook.aliyuniot.module;

import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;

@IocBean
@At("/luat/update")
public class LuatUpdateModule {

    @At("/get")
    public void get(HttpServletResponse resp) {
        
    }
}
