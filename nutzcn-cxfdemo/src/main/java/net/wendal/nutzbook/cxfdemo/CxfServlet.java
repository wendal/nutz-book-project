package net.wendal.nutzbook.cxfdemo;

import javax.jws.WebService;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

// 默认情况下, @WebServlet是会扫描到的,但如果这个类打包进jar了,tomcat/jetty就不会扫描了
// 或者把它配到web.xml也行,这样Setup.init里面就不需要主动添加了
@WebServlet(urlPatterns = {"/ws/*"}, name = "cxf")
public class CxfServlet extends CXFNonSpringServlet {

    private static final long serialVersionUID = 4864173041589214288L;
    private static final Log log = Logs.get();

    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);
        Bus b = getBus();
        BusFactory.setDefaultBus(b);

        // 首先,拿到ioc容器
        Ioc ioc = CxfdemoMainSetup.ioc;
        for (Class<?> klass : Scans.me().scanPackage(CxfServlet.class, null)) {
            // 有@WebService和@IocBean注解的非接口类
            WebService ws = klass.getAnnotation(WebService.class);
            if (ws == null || klass.isInterface())
                continue;
            if (Strings.isBlank(ws.serviceName())) {
                log.infof("%s has @WebService but serviceName is blank, ignore", klass.getName());
                continue;
            }
            log.debugf("add WebService addr=/%s type=%s", ws.serviceName(), klass.getName());
            JaxWsServerFactoryBean sfb = new JaxWsServerFactoryBean();
            // 不需要设置服务接口类
            // sfb.setServiceClass(klass);
            // 不需要服务请求路径
            // sfb.setAddress("/" + ws.serviceName());
            // 设置服务实现类
            sfb.setServiceBean(ioc.get(klass));
            sfb.create();
        }

    }
}
