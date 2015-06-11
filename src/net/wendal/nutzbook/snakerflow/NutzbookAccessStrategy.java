package net.wendal.nutzbook.snakerflow;

import java.util.ArrayList;
import java.util.List;

import net.wendal.nutzbook.bean.Role;
import net.wendal.nutzbook.bean.User;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.snaker.engine.impl.GeneralAccessStrategy;

@IocBean
public class NutzbookAccessStrategy extends GeneralAccessStrategy {

	@Inject
	protected Dao dao;

	@Override
	protected List<String> ensureGroup(String operator) {
		List<String> groups = new ArrayList<String>();
		
		User user = dao.fetchLinks(dao.fetch(User.class, operator), "roles");
		if (user != null) {
			groups.add(operator);
			if (user.getRoles() != null) {
				for (Role role : user.getRoles()) {
					groups.add(role.getName());
				}
			}
		}
		groups.add("admin"); // admin 总是有权限执行任何调整
		return groups;
	}
	
}
