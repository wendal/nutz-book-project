package net.wendal.nutzbook.cxfdemo.webservice;

import org.nutz.integration.cxf.AbstractCxfModule;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean(create = "_init", depose = "depose")
@At("/cxf")
public class CxfModule extends AbstractCxfModule {
    
    private static final long serialVersionUID = 1L;

    @Ok("void")
    @Fail("void")
    @At("/*")
    public void service() throws Exception {
        super.service();
    }
}
