package net.wendal.nutzbook.module;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wendal.nutzbook.annotation.SLog;
import net.wendal.nutzbook.bean.Role;
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
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.StreamHelper;

@IocBean
@At("/admin/process")
@SLog(tag="流程", msg="")
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
		//log.debug("model json=\n" + json);
		return json;
	}

	/**
	 * 下载或读取xml格式的流程定义
	 */
	@At("/xml/?")
	@Ok("raw:xml")
	@SLog(tag="下载定义.XML", msg="流程定义id=${args[0]}")
	public Object processXml(String pid, HttpServletResponse resp) throws UnsupportedEncodingException {
		if (Strings.isBlank(pid))
			return HttpStatusView.HTTP_404;
		org.snaker.engine.entity.Process p = snakerEngine.process().getProcessById(pid);
		if (p == null)
			return HttpStatusView.HTTP_404;
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(p.getName(), Encoding.UTF8) + ".xml\"");
		return p.getDBContent();
	}
	
	@Ok("json")
	@At("/deploy")
	public boolean processDeploy(@Param("model")String model,
								 @Param("id")String id,
								 @Param("savetype")String savetype,
								 @Attr("me")int userId) {
		try {
			if (Strings.isBlank(id) || "new".equals(savetype)) {
				snakerEngine.process().deploy(StreamHelper.getStreamFromString(model));
			} else {
				snakerEngine.process().redeploy(id, StreamHelper.getStreamFromString(model));
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
		NutMap map = new NutMap();
		map.put("ps", ps);
		map.put("pager", pager);
		pager.setRecordCount(1);
		return ajaxOk(map);
	}
	
	@At
	@SLog(tag="禁用流程", msg="定义[${args[0]}]")
	public void delete(@Param("id")String id) {
		snakerEngine.process().undeploy(id);
	}
	
	@At
	@SLog(tag="恢复流程", msg="定义[${args[0]}]")
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
	@SLog(tag="终止流程实例", msg="实例[${args[0]}]")
	public void terminateOrder(String orderId) {
		snakerEngine.order().terminate(orderId, SnakerEngine.ADMIN);
	}
	
	// 流程编辑器相关的方法
	@RequiresRoles("admin")
	@Ok("json:{actived:'id|name'}")
	@At
	public Object users() {
		return dao.query(User.class, null);
	}
	
	@RequiresRoles("admin")
	@Ok("json:{actived:'id|name'}")
	@At
	public Object roles() {
		return dao.query(Role.class, null);
	}
	
	//------------------------------------------
	// 任务流转
	
	@RequiresUser
	@At("/task/?")
	@Ok("json")
	@GET
	public Object task(String taskId) {
		Task task = snakerEngine.query().getTask(taskId);
		if (task == null) {
			return HttpStatusView.HTTP_404;
		}
		return ajaxOk(task);
	}
	
	@AdaptBy(type=JsonAdaptor.class)
	@RequiresUser
	@At("/task/?")
	@Ok("json")
	@POST
	public Object taskComplete(String taskId, @Param("..")NutMap map, @Attr("me")int userId) {
		Task task = snakerEngine.query().getTask(taskId);
		if (task == null) {
			return HttpStatusView.HTTP_404;
		}
		// TODO 不管3721,先成功流转起来再说
		snakerEngine.executeTask(taskId, dao.fetch(User.class, userId).getName());
		return ajaxOk(null);
	}
	
	@RequiresRoles("admin")
	@Ok("json:{actived:'id|name'}")
	@At("/task/?/reassign")
	@SLog(tag="任务转派", msg="任务[${args[0]}],转派给[${args[1]}]")
	public Object taskReassign(String taskId, @Param("to")String to) {
		Task task = snakerEngine.query().getTask(taskId);
		if (task == null) {
			return HttpStatusView.HTTP_404;
		}
		String[] actors = task.getActorIds();
		List<String> list = new ArrayList<String>();
		for (String actor : actors) {
			if (!actor.equals(to)) {
				list.add(actor);
			}
		}
		// 因为原有actors列表必然不为空
		if (list.isEmpty()) { // 证明无需新增或删除
			return ajaxOk(null);
		}
		// 完全不重叠,所以,就需要先插入目标actor,然后再清楚
		if (list.size() == actors.length) {
			snakerEngine.task().addTaskActor(taskId, to);
		}
		snakerEngine.task().removeTaskActor(taskId, list.toArray(new String[list.size()]));
		return ajaxOk(null);
	}
	
	@At("/image/?")
	@Ok("raw")
	public Object image(String processId) {
		org.snaker.engine.entity.Process p = snakerEngine.process().getProcessById(processId);
		if (p == null) {
			return null;
		}
		
		return SnakerHelper.image(p.getDisplayName(), p.getModel(), 1280, 720);
	}
}
