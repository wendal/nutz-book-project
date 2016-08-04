package net.wendal.nutzbook.service;

import java.util.List;

import net.wendal.nutzbook.bean.PermissionCategory;
import net.wendal.nutzbook.page.Pagination;
import net.wendal.nutzbook.util.RedisKey;

import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.service.IdNameEntityService;

@IocBean(fields = "dao")
public class PermissionCategoryService extends IdNameEntityService<PermissionCategory> implements RedisKey {

	public Pagination getListByPager(int pageNumber) {
		Pager pager = dao().createPager(pageNumber, 20);
		List<PermissionCategory> list = dao().query(getEntityClass(), null, pager);
		pager.setRecordCount(dao().count(getEntityClass(), null));
		return new Pagination(pageNumber, 20, pager.getRecordCount(), list);
	}

	public List<PermissionCategory> getList() {
		List<PermissionCategory> list = dao().query(getEntityClass(), null);
		if (Lang.isEmpty(list)) {
			dao().fetchLinks(list, "permissions");
		}
		return list;
	}

	public PermissionCategory insert(PermissionCategory pc) {
		return dao().insert(pc);
	}
}
