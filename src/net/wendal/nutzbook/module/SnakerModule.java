package net.wendal.nutzbook.module;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.ProcessExt;

import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
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
@At("/admin/process")
public class SnakerModule extends BaseModule {
	
	private static final Log log = Logs.get();
	@Inject protected SnakerEngine snakerEngine;

	@Ok("json")
	@At("/deploy")
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
	
	@At("/list")
	@Ok("json:{locked:'binaryData', ignoreNull:true}")
	public Object processList(@Param("..")Pager pager) {
		Page<org.snaker.engine.entity.Process> page = new Page<org.snaker.engine.entity.Process>();
		if (pager.getPageNumber() > 0)
			page.setPageNo(pager.getPageNumber());
		else
			page.setPageNo(1);
		page.setPageSize(pager.getPageSize());
		List<org.snaker.engine.entity.Process> ps = snakerEngine.process().getProcesss(page, new QueryFilter());
		List<ProcessExt> es = new ArrayList<ProcessExt>(ps.size());
		for (org.snaker.engine.entity.Process p : ps) {
			ProcessExt pe = dao.fetch(ProcessExt.class, p.getId());
			if (pe != null)
				dao.fetchLinks(pe, null); // TODO 过滤字段,不然form很大啊
			es.add(pe);
		}
		NutMap map = new NutMap();
		map.put("ps", ps);
		map.put("es", es);
		map.put("pager", pager);
		pager.setRecordCount(1);
		return ajaxOk(map);
	}
	
	@At
	public void delete(@Param("id")String id) {
		snakerEngine.process().undeploy(id);
	}
}
