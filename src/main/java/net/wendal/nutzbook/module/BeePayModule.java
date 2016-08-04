package net.wendal.nutzbook.module;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

@IocBean
@At("/pay/bc")
public class BeePayModule extends BaseModule {
    
    @Inject
    PropertiesProxy conf;

    @POST
    @RequiresUser
    @Ok("json")
    @At
    public Object create(@Param("to")int toUserId, @Param("title")String title, @Param("amount")int amount) {
        String out_trade_no = "u"+toUserId+"z"+R.UU32().substring(0, 8);
        if (amount == 0) {
            amount = R.random(1, 100);
        }
        else if (amount < 1)
            amount = 1;
        else if (amount > 100)
            amount = 100;
        String appid = conf.get("bc.appId");
        String appSecret = conf.get("bc.appSecret");
        if (Strings.isBlank(title))
            title = "随机打赏给uid="+toUserId;
        NutMap re = new NutMap();
        re.put("out_trade_no", out_trade_no);
        re.put("amount", amount);
        re.put("title", title);
        re.put("sign", Lang.md5(appid+title+amount+out_trade_no+appSecret));
        //re.put("debug", true);
        return _map("data", re);
    }
}
