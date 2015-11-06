package net.wendal.nutzbook.module;

import net.wendal.nutzbook.bean.UserProfile;

import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/jetx")
public class JetTemplateModule extends BaseModule {

	@At
	@Ok("jetx:hello.jetx")
	public Object hello() {
		QueryResult qr = new QueryResult();
		Pager pager = dao.createPager(1, 20);
		pager.setRecordCount(dao.count(UserProfile.class));
		qr.setPager(pager);
		qr.setList(dao.query(UserProfile.class, null, pager));
		return qr;
	}
}
