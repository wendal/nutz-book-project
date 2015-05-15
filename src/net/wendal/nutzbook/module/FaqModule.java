package net.wendal.nutzbook.module;

import java.io.IOException;

import net.wendal.nutzbook.bean.FaqItem;
import net.wendal.nutzbook.service.FaqService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@IocBean
@At("/faq")
@Ok("json")
public class FaqModule extends BaseModule {
	
	@Inject FaqService faqService;

	@RequiresPermissions("faq.add")
	@At
	public Object add(@Param("..")FaqItem item) {
		if (item == null || Strings.isBlank(item.getTitle()))
			return ajaxFail("空标题");
		return ajaxOk(dao.insert(item));
	}
	
	@RequiresPermissions("faq.delete")
	@At
	public void delete(@Param("id")long id) {
		dao.delete(id);
	}
	
	@At	
	public Object list(@Param("..")Pager pager) {
		return ajaxOk(query(FaqItem.class, null, pager, "_"));
	}
	
	@At
	public Object match(@Param("key")String key) throws IOException {
		return faqService.top(Strings.splitIgnoreBlank(key, " "));
	}
	
	@RequiresPermissions("faq.update")
	@At
	public Object update(@Param("..")FaqItem item) {
		if (item == null || item.getId() < 1)
			return ajaxFail(null);
		dao.update(item);
		return ajaxOk(null);
	}
}
