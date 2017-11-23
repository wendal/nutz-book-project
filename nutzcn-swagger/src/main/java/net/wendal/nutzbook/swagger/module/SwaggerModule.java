package net.wendal.nutzbook.swagger.module;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.resource.Scans;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.servlet.Reader;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import net.wendal.nutzbook.core.module.BaseModule;

@Api(value = "swagger")
@IocBean(create = "init")
@At("/swagger")
public class SwaggerModule extends BaseModule {

    private static final Log log = Logs.get();

    protected Swagger swagger;

    @GET
    @ApiOperation(value = "Swagger本身的数据", notes = "Swagger的swagger.(json|yaml)数据接口")
    @At
    public void swagger(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String pathInfo = request.getRequestURI();
        if (pathInfo.endsWith("/swagger.json")) {
            response.setContentType("application/json");
            response.getWriter().println(Json.mapper().writeValueAsString(swagger));
        } else if (pathInfo.endsWith("/swagger.yaml")) {
            response.setContentType("application/yaml");
            response.getWriter().println(Yaml.mapper().writeValueAsString(swagger));
        } else {
            response.setStatus(404);
        }
    }

    @GET
    @ApiOperation(value = "心跳接口", notes = "发我一个ping,回你一个pong")
    @At
    @Ok("json:full")
    public Object ping() {
        return new NutMap("ok", true).setv("data", "pong");
    }

    @POST
    @ApiOperation(value = "回显接口", notes = "发我一个字符串,原样回复一个字符串")
    @ApiImplicitParams({@ApiImplicitParam(name = "text", paramType="form", value = "想发啥就发啥", dataType="string", required = true)})
    @At
    @Ok("raw")
    public String echo(@Param("text") String text) {
        return text;
    }

    @At("/")
    @Ok("->:/assets/swagger/index.html")
    public void index() {}

    public void init() {
        log.info("init swagger ...");
        Info info = new Info();
        info.title("NutzCN");
        swagger = new Swagger();
        swagger.setInfo(info);
        swagger.setBasePath(Mvcs.getServletContext().getContextPath());
        HashSet<Class<?>> classes = new HashSet<>();
        for (Class<?> klass : Scans.me().scanPackage("net.wendal.nutzbook.swagger")) {
            classes.add(klass);
        }
        Reader.read(swagger, classes);
    }
}
