package net.wendal.nutzbook.module.demo;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.Uploads;

import org.nutz.plugins.apidoc.annotation.Api;
import net.wendal.nutzbook.bean.User;

@Api(name="演示", description="演示各种用法")
@IocBean
@At("/demo")
public class SimpleDemoModule {

    @At("/param/mydate")
    public void mydate(@Param("..")P p) {
        System.out.println(Json.toJson(p));
    }
    
    public static class P {
        public Date mydate;
    }
    
//    @At
//    @AdaptBy(type=UploadAdaptor.class)
//    public void upload(@Param("file")TempFile tmp, @Param("file")List<TempFile> files,
//                       @Param("file")List<File> files2) throws IOException{
//        System.out.println(tmp.getFile().getAbsolutePath());
//        //if (tmp != null)
//        //    tmp.delete();
//        System.out.println(tmp);
//        System.out.println(files);
//        System.out.println(files.get(0));
//        System.out.println(files2);
//    }
    
    @At
    @Ok("json")
    public Object uploadp(HttpServletRequest req) {
        return Uploads.getInfo(req);
    }
    
    @Ok("json")
    @At("/whale/test")
    @AdaptBy(type=WhaleAdaptor.class)
    public Object whaleInput(@Param("lines")String[] lines) {
        return lines;
    }
    
    @AdaptBy(type=JsonAdaptor.class)
    @At("/pathjson/**")
    public void pathjson(NutMap map) {
        System.out.println(Json.toJson(map));
    }
    
    @At("/get/?/?/?")
    @Ok("json")
    public void get(String host,String startTime,String endTime) {
        
    }

    @At("/url")
    @Ok("json")
    public void url(String url) {
        System.out.println(url);
    }
    
    @At("/play/mp3")
    @Ok("raw")
    public File play_mp3() {
        return new File("D:\\05472810ed6b26f5c90cc02d41e08865.mp3");
    }
    
    @At("/dw/mp3")
    @Ok("raw")
    public File download_mp3() {
        return new File("D:\\05472810ed6b26f5c90cc02d41e08865.mp3");
    }
    
    @At(value="/notfound",top=false)
    @Ok("raw")
    public String notfound() {
        return "404了啊啊啊";
    }
    
    @At("/param/list")
    @Ok("json:forlook")
    public Object paramList(@Param("::user")List<User> users) {
        return users;
    }
}
