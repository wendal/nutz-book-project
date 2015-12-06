package net.wendal.nutzbook.module.admin;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/admin/frame")
public class FrameAct {

	/** 用户管理 **/
	@At("/user_main")
	@Ok("fm:templates.admin.user.frame.main")
	public void userMain() {
	}

	@At("/user_left")
	@Ok("fm:templates.admin.user.frame.left")
	public void userLeft() {
	}

	@At("/user_right")
	@Ok("fm:templates.admin.user.frame.right")
	public void userRight() {
	}

	// 系统配置
	@At("/user_pwd_main")
	@Ok("fm:templates.admin.account.frame.main")
	public void gamePwdMain() {
	}

	@At("/user_pwd_left")
	@Ok("fm:templates.admin.account.frame.left")
	public void gamePwdLeft() {
	}

	@At("/user_pwd_right")
	@Ok("fm:templates.admin.account.frame.right")
	public void gamePwdRight() {
	}

	@At("/topic_main")
	@Ok("fm:templates.admin.topic.frame.main")
	public void topicMain() {
	}

	@At("/topic_left")
	@Ok("fm:templates.admin.topic.frame.left")
	public void topicLeft() {
	}

	@At("/topic_right")
	@Ok("fm:templates.admin.topic.frame.right")
	public void topicRight() {
	}
}
