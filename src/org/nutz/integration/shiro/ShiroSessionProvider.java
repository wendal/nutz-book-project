package org.nutz.integration.shiro;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.nutz.mvc.SessionProvider;

public class ShiroSessionProvider implements SessionProvider {

	public HttpServletRequest filter(HttpServletRequest req, HttpServletResponse resp, ServletContext servletContext) {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key");
		return new ShiroHttpServletRequest(req, servletContext, true);
	}

	public void notifyStop() {
	}

}