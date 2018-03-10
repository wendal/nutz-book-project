package net.wendal.nutzbook.core.ioc;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

import okhttp3.OkHttpClient;

@IocBean
public class EthBeans {

    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @IocBean(name="web3jService")
    public Web3jService createWeb3jService() {
        String url = conf.get("web3j.http.url", HttpService.DEFAULT_URL);
        boolean debug = conf.getBoolean("web3j.http.debug", true);
        boolean includeRawResponses = conf.getBoolean("web3j.http.includeRawResponses", false);
        if (debug)
            return new HttpService(url, includeRawResponses);
        return new HttpService(url, new OkHttpClient.Builder().build(), includeRawResponses);
    }
    
    @IocBean(name="web3j")
    public Web3j createWeb3j() {
        return Web3j.build(ioc.get(Web3jService.class, "web3jService"));
    }
    
    @IocBean(name="web3jAdmin")
    public Admin createWeb3jAdmin() {
        return Admin.build(ioc.get(Web3jService.class, "web3jService"));
    }
}
