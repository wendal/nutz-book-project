package net.wendal.nutzbook.uflo.module;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

import com.bstek.uflo.console.UfloServlet;
import com.bstek.uflo.service.SchedulerService;

/**
 * 将UfloServlet代理为一个入口方法咯
 * @author wendal
 *
 */
@IocBean(create="_init") //不能直接写init,因为父类的init方法是带参数
public class UfloModule extends UfloServlet {

    private static final long serialVersionUID = 1L;
    
    @Inject
    protected SchedulerService schedulerService;

    @Ok("void")
    @Fail("http:500")
    @At("/uflo/*")
    public void uflo() throws ServletException, IOException {
        super.service(Mvcs.getReq(), Mvcs.getResp());
    }
    
    public void _init() throws ServletException {
        final NutMap params = new NutMap();
        init(new ServletConfig() {
            public String getServletName() {
                return "uflo";
            }

            public ServletContext getServletContext() {
                return Mvcs.getServletContext();
            }

            public Enumeration<String> getInitParameterNames() {
                return new Vector<String>(params.keySet()).elements();
            }

            public String getInitParameter(String name) {
                return params.getString(name);
            }
        });
    }
}
