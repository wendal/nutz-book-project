package net.wendal.nutzbook.weixin.service;

import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxHandler;

public interface WeixinService {

    WxApi2 getWxApi();
    
    WxHandler getHandler();
}
