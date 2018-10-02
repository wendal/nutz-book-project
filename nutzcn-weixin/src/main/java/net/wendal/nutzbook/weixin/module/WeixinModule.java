package net.wendal.nutzbook.weixin.module;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.weixin.spi.WxLogin;
import org.nutz.weixin.spi.WxResp;
import org.nutz.weixin.util.Wxs;

import net.wendal.nutzbook.common.util.Toolkit;
import net.wendal.nutzbook.core.bean.User;
import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.weixin.bean.WeixinUser;
import net.wendal.nutzbook.weixin.service.WeixinService;

@IocBean
@At("/weixin")
public class WeixinModule extends BaseModule {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected WeixinService weixinService;
    
    @At({"/msgin", "/msgin/?"})
    @Fail("http:200")
    public View msgIn(String key, HttpServletRequest req) throws IOException {
        return Wxs.handle(weixinService.getHandler(), req, key);
    }

    @GET
    @At
    @Ok(">>:${obj}")
    public Object qrconnect(HttpSession session) {
        String state = R.UU32();
        session.setAttribute("wxlogin.state", state);
        WxLogin wxLogin = weixinService.getWxLogin();
        if (wxLogin == null) {
            return HTTP_502;
        }
        return wxLogin.qrconnect(Mvcs.getServletContext().getContextPath() + "/weixin/qrconnect/callback", null, state);
    }
    
    @GET
    @At("/qrconnect/callback")
    @Ok(">>:${obj}")
    public Object qrconnect_callback(@Param("state")String state, 
                                     @Param("code")String code,
                                     @Attr(value="wxlogin.state", scope=Scope.SESSION)String _state) {
        //if (Strings.isBlank(state) || !state.equals(_state))
        //    return HTTP_500;
        WxLogin wxLogin = weixinService.getWxLogin();
        if (wxLogin == null) {
            return HTTP_502;
        }
        if (Strings.isBlank(code)) {
            log.debug("微信登录失败, code是null");
        }
        WxResp resp = wxLogin.access_token(code);
        if (resp.getInt("errcode", 0) != 0) {
            log.debugf("resp="+resp);
            return "/";
        }
        Subject subject = SecurityUtils.getSubject();
        String openid = resp.getString("openid");
        if (subject.isAuthenticated()) { // 已经登录过了, 看来是绑定操作
            long uid = Toolkit.uid();
            Cnd cnd = Cnd.where("userId", "=", uid).and("openid", "=", openid);
            WeixinUser wxUser = dao.fetch(WeixinUser.class, cnd);
            if (wxUser == null) {
                resp = wxLogin.userinfo(openid, resp.getString("access_token"));
                wxUser = Lang.map2Object(resp, WeixinUser.class);
                wxUser.setUserId(uid);
                dao.insert(wxUser);
            }
            return "/";
        } else {
            Cnd cnd = Cnd.where("openid", "=", openid);
            WeixinUser wxUser = dao.fetch(WeixinUser.class, cnd);
            if (wxUser == null) {
                User user = userService.add("wx_"+R.UU32().substring(6), R.UU32());
                resp = wxLogin.userinfo(openid, resp.getString("access_token"));
                wxUser = Lang.map2Object(resp, WeixinUser.class);
                wxUser.setUserId(user.getId());
                dao.insert(wxUser);
            }
            subject.login(new SimpleShiroToken(wxUser.getUserId()));
        }
        return "/";
    }
}
