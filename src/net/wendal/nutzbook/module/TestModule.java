package net.wendal.nutzbook.module;

import java.util.List;

import net.wendal.nutzbook.bean.User;

import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Sender;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@IocBean
@At("/test")
public class TestModule {

	@Ok("json")
	@At
	public Object p(@Param("::user")List<User> users) {
		return users;
	}
	
	
	@Ok("raw:html")
	@At
	public String rawHtml(){
		return "<html><head></head><body>123</body></html>";
	}
	
	@AdaptBy(type=JsonAdaptor.class)
	@At
	@Ok("json")
	public Object json_list(@Param("list")List<User> users, @Param("..")Object obj) {
		System.out.println(Json.toJson(obj));
		System.out.println(users.get(0).getClass());
		return Json.toJson(users);
	}
	
	public static void main(String[] args) {
		Request req = Request.create("http://127.0.0.1:8080/nutzbook/test/json_list", METHOD.POST);
		req.setData("{list:[{name:'wendal'}, {name:'peter'}]}");
		String str = Sender.create(req).send().getContent();
		System.out.println(str);
	}
}
