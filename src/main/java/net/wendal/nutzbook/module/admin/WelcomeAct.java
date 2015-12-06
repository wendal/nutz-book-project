package net.wendal.nutzbook.module.admin;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class WelcomeAct {

	@At("/admin/index_main")
	@Ok("fm:templates.admin.main")
	public void index() {
	}

	@At("/admin/top")
	@Ok("fm:templates.admin.top")
	@RequiresUser
	public boolean top() {
		return true;
	}

	@At("/admin/main")
	@Ok("fm:templates.admin.index")
	public void main() {
	}

	@At("/admin/left")
	@Ok("fm:templates.admin.left")
	public void left() {
	}

	@At("/admin/right")
	@Ok("fm:templates.admin.right")
	public void right() {
	}
}
