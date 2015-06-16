package net.wendal.nutzbook.module;

import net.wendal.nutzbook.bean.Role;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.service.DynamicFormService;
import net.wendal.nutzbook.service.UserService;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

/**
 * 适配弹出对话框
 * @author wendal
 *
 */
@At("/dialog")
@IocBean
public class DialogModule extends BaseModule {

	@Inject DynamicFormService dynamicFormService;
	
	@Inject UserService userService;
	
	@RequiresUser
	@At
	@Ok("jsp:jsp.dialog.form")
	public Object form() {
		return dynamicFormService.list(null, null);
	}
	
	@RequiresUser
	@At
	@Ok("jsp:jsp.dialog.assignee")
	public Object assignee() {
		NutMap re = new NutMap();
		re.put("users", dao.query(User.class, null));
		re.put("roles", dao.query(Role.class, null));
		return re;
	}
}
