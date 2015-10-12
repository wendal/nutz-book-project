package net.wendal.nutzbook.beetl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.ext.nutz.BeetlView;
import org.beetl.ext.nutz.BeetlViewMaker;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

public class BeetlViewMaker2 extends BeetlViewMaker {
	
	public static final Log log = Logs.get();

	public BeetlViewMaker2() throws IOException {
		super();
	}

	public void init() {
		try {
			super.init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// 添加全局变量
		Map<String, Object> share = groupTemplate.getSharedVars();
		if (share == null) {
			share = new NutMap();
			groupTemplate.setSharedVars(share);
		}
		Ioc ioc = Mvcs.getIoc();
		share.put("ioc", ioc);
		PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");
		share.put("conf", conf.toMap());

		if (!conf.getBoolean("cdn.enable", false) || Strings.isBlank(conf.get("cdn.urlbase"))) {
			share.put("cdnbase", "");
		} else {
			share.put("cdnbase", conf.get("cdn.urlbase"));
			MarkdownFunction.cdnbase = conf.get("cdn.urlbase");
		}
	}
	
	@Override
	public View make(Ioc ioc, String type, String value) {
		if ("beetl".equals(type))
            return new BeetlView(render, value) {
			@Override
			public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
				Stopwatch sw = Stopwatch.begin();
				super.render(req, resp, obj);
				sw.stop();
				log.debug("render time=" + sw.getDuration());
			}
		};
        return null;
	}
}
