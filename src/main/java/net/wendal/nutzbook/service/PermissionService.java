package net.wendal.nutzbook.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wendal.nutzbook.bean.Permission;
import net.wendal.nutzbook.page.Pagination;

import org.apache.commons.lang.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

/**
 * @author 科技㊣²º¹³<br />
 *         2014年2月3日 下午4:48:45<br />
 *         http://www.rekoe.com<br />
 *         QQ:5382211
 */
@IocBean(fields = "dao")
public class PermissionService extends IdEntityService<Permission> {

	public PermissionService() {
	}

	public PermissionService(Dao dao) {
		super(dao);
	}

	public List<Permission> list() {
		return query(null, null);
	}

	public Map<Long, String> map() {
		Map<Long, String> map = new HashMap<Long, String>();
		List<Permission> permissions = query(null, null);
		for (Permission permission : permissions) {
			map.put(permission.getId(), permission.getName());
		}
		return map;
	}

	public Permission insert(Permission permission) {
		return dao().insert(permission);
	}

	public Permission view(Long id) {
		return fetch(id);
	}

	public int update(Permission permission) {
		return dao().update(permission);
	}

	public Pagination getListByPager(Integer pageNumber) {
		return getListByPager(pageNumber, null);
	}

	public Pagination getListByPager(int pageNumber, String permissionCategoryId) {
		int pageSize = 20;
		Cnd cnd = Cnd.where("permissionCategoryId", "=", permissionCategoryId);
		Pager pager = dao().createPager(pageNumber, pageSize);
		List<Permission> list = dao().query(Permission.class, StringUtils.isBlank(permissionCategoryId) ? null : cnd, pager);
		pager.setRecordCount(dao().count(Permission.class, StringUtils.isBlank(permissionCategoryId) ? null : cnd));
		return new Pagination(pageNumber, pageSize, pager.getRecordCount(), list);
	}
}
