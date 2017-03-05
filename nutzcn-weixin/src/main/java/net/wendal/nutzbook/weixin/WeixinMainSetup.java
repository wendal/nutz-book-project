package net.wendal.nutzbook.weixin;

import org.nutz.dao.Dao;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.nutzbook.weixin.bean.WeixinUser;

public class WeixinMainSetup implements Setup {

    public void init(NutConfig nc) {
        nc.getIoc().get(Dao.class).create(WeixinUser.class, false);
    }

    public void destroy(NutConfig nc) {}

}
