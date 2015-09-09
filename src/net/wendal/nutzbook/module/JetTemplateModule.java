package net.wendal.nutzbook.module;

import java.util.List;

import net.wendal.nutzbook.bean.UserProfile;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/jetx")
public class JetTemplateModule extends BaseModule {

	@At
	@Ok("jetx:hello.jetx")
	public Object hello() {
		List<UserProfile> users = dao.query(UserProfile.class, null, dao.createPager(1, 10));
		return new NutMap().setv("users", users).setv("count", dao.count(UserProfile.class));
	}
}
