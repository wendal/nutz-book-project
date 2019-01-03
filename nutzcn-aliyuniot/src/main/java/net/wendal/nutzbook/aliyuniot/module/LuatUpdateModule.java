package net.wendal.nutzbook.aliyuniot.module;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/luat")
public class LuatUpdateModule {

    @Fail("http:500")
    @Ok("void")
    @At("/update")
    public void update(String project_key, String imei, String firmware_name, String version, int need_oss_url, HttpServletResponse resp) throws FileNotFoundException, IOException {
        // TODO 根据 imei 查出设备
        // TODO 根据设备查出升级计划
        String expectVersion = "2.0.1";
        // 判断版本号
        if (expectVersion.equalsIgnoreCase(version)) {
            resp.setStatus(404); // 不需要升级
            return;
        }
        try (FileInputStream ins = new FileInputStream("/data/luat/update/" + expectVersion + "/update.bin")) {
            Streams.writeAndClose(resp.getOutputStream(), ins);
        }
    }
}
