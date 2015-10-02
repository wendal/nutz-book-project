package net.wendal.nutzbook.beetl;

import java.io.IOException;
import java.util.Map;

import org.beetl.ext.nutz.BeetlViewMaker;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;

public class BeetlViewMaker2 extends BeetlViewMaker {

	public BeetlViewMaker2() throws IOException {
		super();
	}

	public void init() throws IOException {
		super.init();
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

		if (!conf.getBoolean("cdn.enable", false) || Strings.isBlank(conf.get("cdn.urlbase.rs"))) {
			share.put("rsbase", Mvcs.getServletContext().getContextPath() + "/rs");
		} else {
			share.put("rsbase", conf.get("cdn.urlbase.rs"));
		}

		// 上传的图片路径, 尚未使用
		if (!conf.getBoolean("cdn.enable", false) || Strings.isBlank(conf.get("cdn.urlbase.image_upload"))) {
			share.put("imguploadbase", Mvcs.getServletContext().getContextPath() + "/upload");
		} else {
			share.put("imguploadbase", conf.get("cdn.urlbase.image_upload"));
		}
	}
}
