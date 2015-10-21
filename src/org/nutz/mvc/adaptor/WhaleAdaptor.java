package org.nutz.mvc.adaptor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

public class WhaleAdaptor extends PairAdaptor {

	protected Object getReferObject(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, String[] pathArgs) {
		if (req.getHeader("Content-Type") != null) {
			if (req.getHeader("Content-Type").contains("application/x-www-form-urlencoded")) {
				return super.getReferObject(sc, req, resp, pathArgs);
			}
			if (req.getHeader("Content-Type").contains("json")) {
				try {
					return Json.fromJson(req.getReader());
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		return super.getReferObject(sc, req, resp, pathArgs);
	}
	
}
