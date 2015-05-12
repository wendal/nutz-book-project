package net.wendal.nutzbook.module;

import java.io.IOException;
import java.util.List;

import net.wendal.nutzbook.bean.FaqItem;
import net.wendal.nutzbook.service.FaqService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
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
		NutMap re = new NutMap();
		if (item == null || Strings.isBlank(item.getTitle()))
			return re.setv("ok", false);
		return dao.insert(item);
	}
	
	@RequiresPermissions("faq.delete")
	@At
	public void delete(@Param("id")long id) {
		dao.delete(id);
	}
	
	@At	
	public List<FaqItem> list(@Param("..")Pager pager) {
		if (pager.getPageNumber() < 1)
			pager.setPageNumber(1);
		return dao.query(FaqItem.class, null, pager);
	}
	
	@At
	public Object match(@Param("key")String key) throws IOException {
		return faqService.top(Strings.splitIgnoreBlank(key, " "));
	}
	
	@RequiresPermissions("faq.update")
	@At
	public Object update(@Param("..")FaqItem item) {
		NutMap re = new NutMap();
		if (item == null || item.getId() < 1)
			return re.setv("ok", "false");
		dao.update(item);
		return re.setv("ok", true);
	}
}
