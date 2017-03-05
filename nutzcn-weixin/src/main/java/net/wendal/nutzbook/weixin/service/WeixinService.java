package net.wendal.nutzbook.weixin.service;

import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxHandler;
import org.nutz.weixin.spi.WxLogin;

public interface WeixinService {

    WxApi2 getWxApi();
    
    WxHandler getHandler();
    
    WxLogin getWxLogin();
}
