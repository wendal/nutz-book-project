package net.wendal.nutzbook.module.admin2;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.util.Toolkit;

@IocBean
@At("/admin2")
@Fail("http:500")
public class Admin2Module {
	
	@Inject Dao dao;

	@RequiresUser
	@At("/")
	@Ok(">>:index.html")
	public void index() {}
	
	@RequiresUser
	@At("/?")
	@Ok("fm:templates.admin2.${pathargs[0]}")
	public Context page(String _page) {
		return ctx();
	}
	
	@RequiresUser
	@At("/?/?")
	@Ok("fm:templates.admin2.${pathargs[0]}.${pathargs[1]}")
	public Context page2(String _page) {
		return ctx();
	}
	
	public Context ctx() {
		return Lang.context().set("me", dao.fetch(UserProfile.class, Toolkit.uid()));
	}
	
	@At("/user/logout")
	@Ok(">>:/admin2/user/login")
	public void logout() {
		SecurityUtils.getSubject().logout();
	}
}
