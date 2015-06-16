package net.wendal.nutzbook.module;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.snakerflow.SnakerHelper;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
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
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Task;
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
		String json = SnakerHelper.getModelJson(p.getModel());
		json = Json.toJson(Json.fromJson(json));
		log.debug("model json=\n" + json);
		return json;
	}

	@At("/xml/?")
	@Ok("raw")
	public Object processXml(String pid) {
		if (Strings.isBlank(pid))
			return null;
		org.snaker.engine.entity.Process p = snakerEngine.process().getProcessById(pid);
		return p.getDBContent();
	}
	
	@Ok("json")
	@At("/deploy")
	public boolean processDeploy(@Param("model")String model,
								 @Param("id")String id,
								 @Param("savetype")String savetype,
								 @Attr("me")int userId,
								 @Param("svg")String svg) {
		//log.debug("SVG = " + svg);
		try {
			//log.debug("snaker xml=\n" + model);
			if (Strings.isBlank(id) || "new".equals(savetype)) {
				snakerEngine.process().deploy(StreamHelper.getStreamFromString(model));
			} else {
				snakerEngine.process().redeploy(id, StreamHelper.getStreamFromString(model));
			}
			//System.out.println("PUT: " + pid + "... " + svg);
			//svgs.put(pid, svg);
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
		NutMap map = new NutMap();
		map.put("ps", ps);
		map.put("pager", pager);
		pager.setRecordCount(1);
		return ajaxOk(map);
	}
	
	@At
	public void delete(@Param("id")String id) {
		snakerEngine.process().undeploy(id);
	}
	
	@At
	public void resume(@Param("id")String id) {
		dao.update("wf_process", Chain.make("state", 1), Cnd.where("id", "=", id));
	}
	
	@At("/start/?")
	@RequiresUser
	public Object start(String processId, @Attr("me")int userId) {
		User user = dao.fetch(User.class, userId);
		Order order = snakerEngine.startInstanceById(processId, user.getName());
		//snakerEngine.task().
		QueryFilter qf = new QueryFilter();
		qf.setOperator(user.getName());
		qf.setOrderId(order.getId());
		List<Task> tasks = snakerEngine.query().getActiveTasks(qf);
		System.out.println(Json.toJson(tasks));
		return tasks;
	}
	
	@Ok("json")
	@At
	public Object tasks(@Attr("me")int userId, @Param("..")Pager pager) {
		User user = dao.fetch(User.class, userId);
		QueryFilter qf = new QueryFilter();
		qf.setOperator(user.getName());

		if (pager == null)
			pager = new Pager();
		Page<Task> page = new Page<Task>();
		List<Task> tasks = snakerEngine.query().getActiveTasks(toPage(pager, page), qf);
		pager.setRecordCount((int) page.getTotalCount());
		
		NutMap re = new NutMap();
		re.put("pager", pager);
		re.put("tasks", tasks);
		List<Order> orders = new ArrayList<Order>();
		for (Task task : tasks) {
			orders.add(snakerEngine.query().getOrder(task.getOrderId()));
		}
		re.put("orders", orders);
		
		List<org.snaker.engine.entity.Process> ps = new ArrayList<org.snaker.engine.entity.Process>(orders.size());
		for (Order order : orders) {
			if (order == null) {
				ps.add(null);
			} else {
				ps.add(snakerEngine.process().getProcessById(order.getProcessId()));
			}
		}
		re.put("ps", ps);
		return ajaxOk(re);
	}
	
	@At
	@Ok("json")
	public Object orders(@Param("..")Pager pager) {
		if (pager == null)
			pager = new Pager();
		Page<Order> page = new Page<Order>();
		List<Order> orders = snakerEngine.query().getActiveOrders(toPage(pager, page), new QueryFilter());
		pager.setRecordCount((int) page.getTotalCount());
		NutMap re = new NutMap();
		re.put("pager", pager);
		re.put("orders", orders);
		List<org.snaker.engine.entity.Process> ps = new ArrayList<org.snaker.engine.entity.Process>(orders.size());
		List<List<Task>> tasks = new ArrayList<List<Task>>(orders.size());
		for (Order order : orders) {
			ps.add(snakerEngine.process().getProcessById(order.getProcessId()));
			QueryFilter filter = new QueryFilter();
			filter.setOrderId(order.getId());
			List<Task> t = snakerEngine.query().getActiveTasks(filter);
			tasks.add(t);
		}
		re.put("ps", ps);
		re.put("tasks", tasks);
		return ajaxOk(re);
	}
	
	/**
	 * nutz的Pager转snaker的Page
	 * @param pager
	 * @return
	 */
	protected <T> Page<T> toPage(Pager pager, Page<T> page) {
		page.setPageNo(pager.getPageNumber());
		page.setPageSize(pager.getPageSize());
		return page;
	}
	
	/**
	 * 终止流程实例
	 */
	@RequiresRoles("admin")
	@At("/order/?/terminate")
	@Ok("json")
	public void terminateOrder(String orderId) {
		snakerEngine.order().terminate(orderId, SnakerEngine.ADMIN);
	}
}
