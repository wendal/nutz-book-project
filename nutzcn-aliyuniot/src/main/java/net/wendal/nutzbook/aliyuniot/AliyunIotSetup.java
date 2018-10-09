package net.wendal.nutzbook.aliyuniot;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.nutzbook.aliyuniot.bean.AliyunDev;
import net.wendal.nutzbook.aliyuniot.service.AliyunIotService;

@IocBean
public class AliyunIotSetup implements Setup {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected AliyunIotService aliyunIotService;

    @Override
    public void init(NutConfig nc) {
        Dao dao = nc.getIoc().get(Dao.class);
        Daos.migration(dao, AliyunDev.class, true, false, false);
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
