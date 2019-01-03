package net.wendal.nutzbook.aliyuniot.jsobj;

import org.nutz.http.Http;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

@IocBean
public class JsSmsObj {

    @Inject
    protected PropertiesProxy conf;
    
    public String sendAsync(String mobile, String text) {
        String apiUrl = "https://sms.yunpian.com/v2/sms/single_send.json";
        String apikey = conf.get("yunpian.apikey");
        NutMap params = new NutMap();
        params.put("apikey", apikey);
        params.put("mobile", mobile);
        params.put("text", text);
        return Http.post(apiUrl, params, conf.getInt("yunpian.sendTimeout", 5000));
    }
}
