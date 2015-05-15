package net.wendal.nutzbook.module;

import net.wendal.nutzbook.bean.UserProfile;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class PageModule {

	@Inject Dao dao;
	
	@RequiresAuthentication
	@At(value={"/", "/index"})
	@Ok(">>:/home")
	public void index() {}
	
//	@RequiresAuthentication
//	@At(value={"/admin/?", "/admin"})
//	@Ok("jsp:jsp.page.${obj}")
//	public String page(String page) {
//		if (Strings.isBlank(page))
//			page = "index";
//		return page;
//	}
	
	@RequiresAuthentication
	@At("/home")
	@Ok("jsp:jsp.home")
	public Object home(@Attr(scope=Scope.SESSION, value="me")long uid) {
		UserProfile profile = Daos.ext(dao, FieldFilter.locked(UserProfile.class, "avatar")).fetch(UserProfile.class, uid);
		if (profile == null)
			profile = new UserProfile();
		return profile;
	}
	
}
