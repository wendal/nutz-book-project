package net.wendal.nutzbook.luat.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.core.module.BaseModule;
import net.wendal.nutzbook.luat.bean.LuatUpgradeRequest;
import net.wendal.nutzbook.luat.bean.LuatUpgradeResp;
import net.wendal.nutzbook.luat.service.LuatUpdateService;

@Fail("http:500")
@IocBean
@At("/luat/upgrade")
public class LuatUpgradeModule extends BaseModule {

    @Inject
    protected LuatUpdateService luatUpgradeService;

    @Inject
    protected PropertiesProxy conf;

    @Fail("http:500")
    @Ok("void")
    @At(value = {"/luat/upgrade/get", "/api/site/firmware_upgrade"}, top = true)
    public void get(@Param("..") LuatUpgradeRequest req, HttpServletResponse resp) throws FileNotFoundException, IOException {
        LuatUpgradeResp _resp = luatUpgradeService.exec(req);
        // 判断版本号
        if (!_resp.isMatched()) {
            resp.setStatus(400); // 不需要升级
            resp.setContentType("application/json");
            Json.toJson(resp.getWriter(), _resp, jf);
            return;
        }
        String path = luatUpgradeService.getPkgPath(_resp.getPkg());
        File f = new File(path);
        resp.setContentLength((int) f.length());
        try (FileInputStream ins = new FileInputStream(f)) {
            Streams.writeAndClose(resp.getOutputStream(), ins);
        }
    }

    @POST
    @Ok("json:full")
    @At
    public NutMap check(@Param("..") LuatUpgradeRequest req) throws FileNotFoundException, IOException {
        LuatUpgradeResp _resp = luatUpgradeService.exec(req);
        return _map("ok", true, "data", _resp);
    }

    protected static JsonFormat jf = JsonFormat.compact().setQuoteName(true).setAutoUnicode(true);
}
