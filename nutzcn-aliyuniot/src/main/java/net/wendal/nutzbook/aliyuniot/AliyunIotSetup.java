package net.wendal.nutzbook.aliyuniot;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.nutzbook.aliyuniot.service.AliyunIotService;

@IocBean
public class AliyunIotSetup implements Setup {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected AliyunIotService aliyunIotService;

    @Override
    public void init(NutConfig nc) {
        try {
            aliyunIotService.startListen();
        }
        catch (Throwable e) {
            log.debug("尝试启动阿里云订阅服务时出错了", e);
        }
    }

    @Override
    public void destroy(NutConfig nc) {
        aliyunIotService.stopListen();
    }

}
