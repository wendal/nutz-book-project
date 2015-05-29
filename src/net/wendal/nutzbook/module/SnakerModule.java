package net.wendal.nutzbook.module;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.ProcessExt;
import net.wendal.nutzbook.snakerflow.SnakerHelper;

import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
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
	
	@At("/design/?")
	@Ok("jsp:/admin/process/design/index.jsp")
	public Object design(String pid, HttpServletRequest req) {
		if (Strings.isBlank(pid))
			return null;
		org.snaker.engine.entity.Process p = snakerEngine.process().getProcessById(pid);
		req.setAttribute("processId", p.getId());;
		return p;
	}
	
	@At("/json/?")
	@Ok("raw")
	public Object processJson(String pid) {
		if (Strings.isBlank(pid))
			return null;
		org.snaker.engine.entity.Process p = snakerEngine.process().getProcessById(pid);
		return SnakerHelper.getModelJson(p.getModel());
	}

	@Ok("json")
	@At("/deploy")
	public boolean processDeploy(@Param("model")String model,
								 @Param("id")String id,
								 @Param("savetype")String savetype,
								 @Attr("me")long userId) {
		try {
			log.debug("snaker xml=\n" + model);
			String pid = id;
			if (Strings.isBlank(id) || "new".equals(savetype)) {
				pid = snakerEngine.process().deploy(StreamHelper.getStreamFromString(model));
			} else {
				snakerEngine.process().redeploy(id, StreamHelper.getStreamFromString(model));
			}
			ProcessExt ext = dao.fetch(ProcessExt.class, pid);
			if (ext == null) {
				ext = new ProcessExt();
				ext.setProcessId(pid);
				ext.setUserId(userId);
				dao.insert(ext);
			} else {
				ext.setUserId(userId);
				dao.update(ext);
			}
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
		QueryFilter queryFilter = new QueryFilter();
		List<org.snaker.engine.entity.Process> ps = snakerEngine.process().getProcesss(page, queryFilter);
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
	
	@At("/ext/update")
	public void updateProcessExt(@Param("..")ProcessExt ext, @Attr("me")long userId) {
		if (ext == null)
			return;
		if (ext.getProcessId() == null)
			return;
		ext.setUserId(userId);
		if (dao.fetch(ProcessExt.class, ext.getProcessId()) != null) {
			dao.updateIgnoreNull(ext);
		} else {
			dao.insert(ext);
		}
	}
}
