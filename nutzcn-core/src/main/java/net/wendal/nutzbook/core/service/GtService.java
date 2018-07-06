package net.wendal.nutzbook.core.service;

import org.nutz.lang.util.NutMap;

public interface GtService {

    NutMap captcha(String userId, String ip);
    
    String verify(String challenge, String validate, String seccode, String userId, String ip);
}
