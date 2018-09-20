package net.wendal.nutzbook.core.service.impl;

import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.wendal.nutzbook.core.service.GtService;

@IocBean(name="gtService")
public class GtServiceImpl implements GtService {

    private static final Log log = Logs.get();
    
    protected String baseurl = "https://api.geetest.com";
    protected String reguri = "/register.php";
    protected String validateuri = "/validate.php";

    @Inject
    protected PropertiesProxy conf;
    
    public NutMap captcha(String userId, String ip) {
        NutMap params = new NutMap();
        params.put("user_id", userId); //网站用户id
        params.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        if (!Strings.isBlank(ip))
            params.put("ip_address", ip); //传输用户请求验证时所携带的IP
        String gt = conf.get("geetest.id");
        String key = conf.get("geetest.key");
        params.put("gt", gt);
        params.put("json_format", "1");
        String url = baseurl + reguri;
        Response resp = Http.post2(url, params, 5000);
        log.debug("geetest register.php status=" + resp.getStatus());
        NutMap re = Json.fromJson(NutMap.class, resp.getContent());
        if (Strings.isBlank(re.getString("challenge")) || re.getString("challenge").length() < 5) {
            log.error("geetest 参数未设定!! 请登录 http://www.geetest.com/ 注册账号并填到geetest.properties文件");
            return re;
        }
        re.put("challenge", Lang.md5(re.getString("challenge") + key));
        re.put("success", 1);
        re.put("gt", gt);
        return re;
    }
    
    public String verify(String challenge, String validate, String seccode, String userId, String ip) {
        NutMap params = new NutMap();
        params.put("user_id", userId); //网站用户id
        params.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        if (!Strings.isBlank(ip))
            params.put("ip_address", ip); //传输用户请求验证时所携带的IP
        params.put("challenge", challenge);
        params.put("validate", validate);
        params.put("seccode", seccode);
        
        String gt = conf.get("geetest.id");
        String key = conf.get("geetest.key");
        
        if (!Lang.md5(key + "geetest" + challenge).equalsIgnoreCase(validate)) {
            log.info("本地校验challenge和validate失败,非法请求");
            return "本地校验challenge和validate失败,非法请求";
        }
        params.put("gt", gt);
        params.put("json_format", "1");
        String url = baseurl + validateuri;
        Response resp = Http.post2(url, params, 5000);
        log.debug("geetest validate.php status=" + resp.getStatus());
        if (resp.isOK()) {
            NutMap re = Json.fromJson(NutMap.class, resp.getContent());
            if (Lang.md5(seccode).equals(re.get("seccode")))
                return null;
            else
                return "远程校验challenge和validate失败,非法请求";
        }
        return "geetest服务器异常,请联系管理员";
    }
}
