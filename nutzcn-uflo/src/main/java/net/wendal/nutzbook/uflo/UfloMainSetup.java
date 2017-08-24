package net.wendal.nutzbook.uflo;

import javax.servlet.ServletContextEvent;

import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.quartz.SchedulerException;
import org.springframework.web.context.ContextLoaderListener;

import com.bstek.uflo.service.SchedulerService;

public class UfloMainSetup implements Setup {
    
    /**
     * 代理Spring的初始化过程
     */
    protected ContextLoaderListener ctx = new ContextLoaderListener();
    
    protected SchedulerService schedulerService;

    @Override
    public void init(NutConfig nc) {
        nc.getServletContext().setInitParameter("contextConfigLocation", "classpath*:spring-context.xml");
        ctx.contextInitialized(new ServletContextEvent(nc.getServletContext()));
        schedulerService = ContextLoaderListener.getCurrentWebApplicationContext().getBean(SchedulerService.class);
    }

    @Override
    public void destroy(NutConfig nc) {
        try {
            // 2.0.0 版的schedulerService的线程池没有自动关闭,那我们就主动关掉它吧
            if (schedulerService != null)
                schedulerService.getScheduler().shutdown(true);
        }
        catch (SchedulerException e) {
        }
        ctx.contextDestroyed(new ServletContextEvent(nc.getServletContext()));
    }

}
