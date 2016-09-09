package net.wendal.nutzbook.module;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.util.Toolkit;

@Api(name="页面模块", description="拦截几个首页请求", match=ApiMatchMode.NONE)
@IocBean
public class PageModule {

	@Inject 
	protected Dao dao;
	
	@At(value={"/", "/index"})
	@Ok("->:/yvr/list")
	public void index() {}
	
	@RequiresAuthentication
	@At("/home")
	@Ok("jsp:jsp.home")
	public Object home() {
		int uid = Toolkit.uid();
		UserProfile profile = Daos.ext(dao, FieldFilter.locked(UserProfile.class, "avatar")).fetch(UserProfile.class, uid);
		if (profile == null)
			profile = new UserProfile();
		return profile;
	}
	
}
