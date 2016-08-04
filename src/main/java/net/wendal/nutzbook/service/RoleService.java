package net.wendal.nutzbook.service;

import java.util.List;

import net.wendal.nutzbook.bean.Role;
import net.wendal.nutzbook.page.Pagination;

import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.service.IdNameEntityService;

@IocBean(fields = "dao")
public class RoleService extends IdNameEntityService<Role> {

	public Pagination getListByPager(int pageNumber) {
		Pager pager = dao().createPager(pageNumber, 20);
		List<Role> list = dao().query(getEntityClass(), null, pager);
		pager.setRecordCount(dao().count(getEntityClass(), null));
		return new Pagination(pageNumber, 20, pager.getRecordCount(), list);
	}

	public List<Role> roleList() {
		return dao().query(getEntityClass(), null);
	}

	public Role insert(Role role) {
		return dao().insert(role);
	}

	public int update(Role role) {
		FieldFilter filter = FieldFilter.create(getEntityClass(), FieldMatcher.make(null, "^(createTime|updateTime)$", true));
		return Daos.ext(dao(), filter).update(role);
	}
	
	public void updateRoleRelation(Role role, List<net.wendal.nutzbook.bean.Permission> perms) {
		dao().clearLinks(role, "permissions");
		role.getPermissions().clear();
		dao().update(role);
		if (!Lang.isEmpty(perms)) {
			role.setPermissions(perms);
			dao().insertRelation(role, "permissions");
		}
	}
}
