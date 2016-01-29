package net.wendal.nutzbook.beetl;

import java.io.IOException;
import java.util.Map;

import org.beetl.ext.nutz.BeetlViewMaker;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.wendal.nutzbook.util.Toolkit;

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
		NutMap re = Toolkit.getTemplateShareVars();
		share.putAll(re);
	}
}
