package net.wendal.nutzbook.module;

import java.util.List;

import net.wendal.nutzbook.bean.User;

import org.nutz.ioc.loader.annotation.IocBean;
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
}
