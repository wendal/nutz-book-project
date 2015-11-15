package net.wendal.nutzbook.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.zbus.SayHelloWorld;

@At("/demo")
@IocBean
public class DemoModule {
	
	private static final Log log = Logs.get();

	@At
	@Ok("json")
	public NutMap list(HttpSession session) throws Exception {
		System.out.println(session.getId());
		return NutMap.NEW();
	}

	@At
	@Ok("json")
	public NutMap login(HttpSession session) {
		System.out.println(session.getId());
		return NutMap.NEW();
	}
	
	/**
	 * 前缀表单嵌套List.<p/>
	 * 页面发送的数据如下:
	 * <p/>
	 * 
	 * <code>user.name=wendal&user.roles[0].id=123</code>
	 */
	@At
	@Ok("json")
	public User test_prefix_list(@Param("::user.")User user, HttpServletRequest req) {
		System.out.println(Json.toJson(req.getParameterMap()));
		System.out.println(Json.toJson(user));
		return user;
	}
	
	/**
	 * 测试RPC调用
	 */
	@At
	@Ok("json")
	public Object rpc() {
		SayHelloWorld sayHelloWorld = Mvcs.getIoc().get(SayHelloWorld.class);
		log.debug(sayHelloWorld.getClass());
		return sayHelloWorld.hi("wendal");
	}
	
	@At("/re/view")
	@Ok("re")
	public Object checkResultView(ViewModel vm) {
		vm.put("hi", "abc");
		return "json";
	}
	
	@At("/re/view2")
	@Ok("re")
	public Object checkResultView2(ViewModel vm) {
		vm.put("hi", "abc");
		return "jsp:/demo/review2";
	}
	
	@At("/upload/beans")
	@Ok("raw")
	@AdaptBy(type=UploadAdaptor.class)
	public void uploadWithBeans(@Param("::user.")User user, @Param("file")TempFile f) {
		System.out.println(Json.toJson(user));
		System.out.println(f.getMeta());
		System.out.println(f.getFile());
	}

	@At("/upload/beans2")
	@AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
	public String uploadWithBeans(
			@Param("::user.") User userp,
			@Param("::children") ArrayList<User> children,
			@Param("cnmlgb") File[] files
			) throws FileNotFoundException {
		System.out.println(files.length);
		System.out.println(Json.toJson(userp));
		System.out.println(Json.toJson(children));
		return "哈哈";
	}
	
	@At("/re/view3")
	@Ok("re:jsp:jsp.home")
	public String test_re_view() {
		return null;
	}
	
	@At("/param/list")
	@Ok("json")
	public Object test_param_list(@Param("::user")List<User> users) {
		return users;
	}
	
	@At("/path/**")
	@Ok("raw")
	public void test_path(String path, HttpServletRequest req) {
		System.out.println(path);
		System.out.println(req.getRequestURI());
	}
}
