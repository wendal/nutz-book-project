package net.wendal.nutzbook.beetl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.web.WebRender;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
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
		Configuration cfg = Configuration.defaultConfiguration();
		WebAppResourceLoader2 resourceLoader = new WebAppResourceLoader2();
		groupTemplate = new GroupTemplate(resourceLoader, cfg);
		log.debug("beetl init complete: root=" + resourceLoader.getRoot());
	}

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
					WebRender render = new WebRender(groupTemplate);
					render.render(child, req, resp);
				}
			};
		return null;
	}
}