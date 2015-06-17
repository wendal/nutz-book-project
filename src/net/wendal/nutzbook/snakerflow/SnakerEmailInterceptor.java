package net.wendal.nutzbook.snakerflow;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.service.EmailService;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.snaker.engine.SnakerInterceptor;
import org.snaker.engine.core.Execution;
import org.snaker.engine.entity.Task;

@IocBean
public class SnakerEmailInterceptor implements SnakerInterceptor {

	private static final Log log = Logs.get();
	
	@Inject
	EmailService emailService;
	
	@Inject
	Dao dao;
	
	public void intercept(Execution execution) {
		if(log.isInfoEnabled()) {
			for(Task task : execution.getTasks()) {
				if(task.getActorIds() != null) {
					for (String actor : task.getActorIds()) {
						if (actor != null && !actor.contains("$")) {
							try {
								User user = dao.fetch(User.class, actor);
								if (user != null) {
									UserProfile profile = dao.fetch(UserProfile.class, user.getId());
									if (profile != null && profile.getEmail() != null && profile.isEmailChecked()) {
										String title = String.format("您有新的流程任务[%s]", task.getDisplayName());
										// TODO 添加跳转链接
										emailService.send(profile.getEmail(), title, "请登录系统查看. 流程任务ID=" + task.getId());
									}
								}
							} catch (Exception e) {
								log.debug("send process task notify email fail", e);
							}
						}
 					}
				}
			}
		}
	}

}
