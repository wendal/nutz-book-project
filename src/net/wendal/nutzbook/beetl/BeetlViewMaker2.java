package net.wendal.nutzbook.beetl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.GroupTemplate;
import org.beetl.ext.web.WebRender;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.view.AbstractPathView;

/**
 * Beelt for Nutz, version 2
 * 
 * @author wendal,joelli
 * 
 */
public class BeetlViewMaker2 implements ViewMaker {

	private static final Log log = Logs.get();

	public static GroupTemplate groupTemplate;

	public BeetlViewMaker2() throws IOException {
		log.debug("beetl init ...");
		groupTemplate = new GroupTemplate(); // 通过配置文件设置ResourceLoader,而非硬编码
		log.debug("beetl init complete");
	}

	/**
	 * 需要用户在@SetupBy指定的Setup类中的depose方法主动调用
	 */
	public static void depose() {
		if (groupTemplate != null)
			groupTemplate.close();
	}

	protected String type = "beetl";

	public View make(Ioc ioc, String type, String value) {
		if (this.type.equals(type))
			return new AbstractPathView(value) {
				public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
					String child = evalPath(req, obj);
					if (child == null) {
						// 当路径为空,选用请求路径作为模板路径
						child = Mvcs.getActionContext().getPath();
					}
					/*
					 * // 如果找不到模板, beetl会使用内置的Error模板进行渲染 //
					 * 渲染前会调用getOutputStream()或getWriter,导致总是Http 200 OK,
					 * ErrorHandler无法改变这个行为 String key = child; int ajaxIdIndex
					 * = key.lastIndexOf("#"); if (ajaxIdIndex != -1) { key =
					 * key.substring(0, ajaxIdIndex); } if
					 * (!groupTemplate.getResourceLoader().exist(key)) {
					 * BeetlException be = new
					 * BeetlException(BeetlException.TEMPLATE_LOAD_ERROR);
					 * be.resourceId = child; throw be; }
					 */
					WebRender render = new WebRender(groupTemplate);
					render.render(child, req, resp);
				}
			};
		return null;
	}
}