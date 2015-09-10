package net.wendal.nutzbook.module;

import net.wendal.nutzbook.bean.UserProfile;

import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/beetl")
@Fail("void")
// 注意, 当前beetl版本下, 除了void视图, @Fail总会失败,因为beetl必然先调用了getOutputStream或getWriter
// 所以其他代码调用sendError或getOutputStream或getWriter总会报错.
// 请使用beetl提供的错误模板机制或ErrorHandler机制
//@Fail("http:500") 
public class BeetlTemplateModule extends BaseModule {

	@At
	@Ok("beetl:hello.html")
	public Object hello() {
		QueryResult qr = new QueryResult();
		Pager pager = dao.createPager(1, 20);
		pager.setRecordCount(dao.count(UserProfile.class));
		qr.setPager(pager);
		qr.setList(dao.query(UserProfile.class, null, pager));
		return qr;
	}
	
	@At
	@Ok("beetl:notExist")
	public void error() {
	}
}
