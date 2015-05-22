package net.wendal.nutzbook.module;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.helper.StreamHelper;

@IocBean
@At("/snaker")
public class SnakerModule extends BaseModule {
	
	private static final Log log = Logs.get();
	@Inject protected SnakerEngine snakerEngine;

	@Ok("json")
	@At("/process/deploy")
	public boolean processDeploy(@Param("model")String model, @Param("id")Integer id, HttpServletRequest req) {
		try {
			log.debug("snaker xml=\n" + model);
			snakerEngine.process().deploy(StreamHelper.getStreamFromString(model));
			return true;
		} catch (Exception e) {
			log.info("deploy snakerflow xml fail", e);
			return false;
		}
	}
	
	@At("/process/list")
	@Ok("json:{locked:'binaryData', ignoreNull:true}")
	public Object processList(@Param("..")Pager pager) {
		Page<org.snaker.engine.entity.Process> page = new Page<org.snaker.engine.entity.Process>();
		if (pager.getPageNumber() > 0)
			page.setPageNo(pager.getPageNumber());
		else
			page.setPageNo(1);
		page.setPageSize(pager.getPageSize());
		return ajaxOk(snakerEngine.process().getProcesss(page, new QueryFilter()));
	}
}
