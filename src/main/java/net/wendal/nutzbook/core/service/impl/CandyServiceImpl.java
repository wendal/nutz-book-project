package net.wendal.nutzbook.core.service.impl;

import org.nutz.aop.interceptor.async.Async;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import net.wendal.nutzbook.core.service.CandyService;
import net.wendal.nutzbook.core.service.EthService;

@IocBean(name="candyService")
public class CandyServiceImpl implements CandyService {

    @Inject
    protected EthService ethService;
    
    @Inject
    protected PropertiesProxy conf;
    
    @Async
    public void sendCandy(String toAcc, int wei) {
        if (conf.getBoolean("candy.enable", false)) {
            String fromAcc = conf.get("candy.master.address");
            String fromAccPassword = conf.get("candy.master.password");
            ethService.sendTransaction(fromAcc, toAcc, fromAccPassword, wei);   
        }
    }
}
