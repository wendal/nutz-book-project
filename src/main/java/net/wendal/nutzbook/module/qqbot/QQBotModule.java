package net.wendal.nutzbook.module.qqbot;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;

/**
 * QQLite机器人入口类
 * Created by wendal on 2015/12/16.
 */
@At("/qqbot")
public class QQBotModule {

    private static final Log log = Logs.get();

    @POST
    @At
    @Ok("raw")
    public String income(@Param("Message")String msg, HttpServletRequest req) {
        log.debug("params==>" + req.getParameterMap());
        String prefix = "@wendal机器人(2096459391) ";
        if (msg == null || !msg.startsWith(prefix) || msg.length() < prefix.length()) {
            return "";
        }
        msg = msg.trim().substring(prefix.length());
        if (msg.isEmpty())
            return "";
        switch (msg) {
            case "帮助":
                return "直接说";
            case "test":
            case "测试":
                return "在的";
        }
        return "";
    }
}
