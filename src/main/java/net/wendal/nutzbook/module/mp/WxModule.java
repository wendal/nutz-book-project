package net.wendal.nutzbook.module.mp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.weixin.impl.BasicWxHandler;
import org.nutz.weixin.util.Wxs;

@At("/api/nutzam/mp")
@IocBean
public class WxModule extends BasicWxHandler {

    /**
     * 微信入口方法
     * @param key 识别号,可以无视
     * @param req http请求
     * @return Wxs会处理好,无视吧
     * @throws IOException 各种IO错误时抛出
     */
    @At({"/in", "/in/?"})
    @Fail("http:200")
    public View msgIn(String key, HttpServletRequest req) throws IOException {
        return Wxs.handle(this, req, key);
    }
    
    // 从配置文件读取token
    @Inject("java:$conf.get('wxmp.token')")
    public void setToken(String token){
        this.token = token;
    }
    
    // 从配置文件读取aeskey
    @Inject("java:$conf.get('wxmp.aes'")
    public void setAesKey(String key){
        this.aesKey = key;
    }
    
    // 从配置文件读取appid
    @Inject("java:$conf.get('wxmp.appid'")
    public void setAppid(String appId){
        this.appId = appId;
    }
}
