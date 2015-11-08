package net.wendal.nutzbook.mvc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutFilter;

public class NutzBookNutFilter extends NutFilter {
	
	protected Set<String> prefixs = new HashSet<String>();
	
	
	public void init(FilterConfig conf) throws ServletException {
		super.init(conf);
		prefixs.add("/druid/");
		prefixs.add("/rs/");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest)req;
			String uri = request.getServletPath() + Strings.sBlank(request.getPathInfo());
			for (String prefix : prefixs) {
				if (uri.startsWith(prefix)) {
					Mvcs.updateRequestAttributes((HttpServletRequest) req);
					chain.doFilter(req, resp);
					return;
				}
			}
		}
		super.doFilter(req, resp, chain);
	}
}
