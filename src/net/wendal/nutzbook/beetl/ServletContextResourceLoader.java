package net.wendal.nutzbook.beetl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.ServletContext;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.misc.BeetlUtil;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

/**
 * 使用ServletContext.getResourceAsStream 加载模板
 * 
 * @author wendal
 *
 */
public class ServletContextResourceLoader implements ResourceLoader {

	private static final Log log = Logs.get();

	protected String root;

	protected Charset charset;

	protected ServletContext servletContext;

	protected boolean autoCheck = true;

	public Resource getResource(final String key) {
		return new Resource(key, this) {

			public Reader openReader() {
				String path = _path(key);
				InputStream ins = servletContext.getResourceAsStream(path);
				if (ins == null) {
					ins = servletContext.getResourceAsStream(path + ".html");
					if (ins == null)
						ins = servletContext.getResourceAsStream(path + ".beetl");
				}
				if (ins == null) {
					log.infof("NOT EXIST KEY=%s PATH=%s", key, path);
					BeetlException be = new BeetlException(BeetlException.TEMPLATE_LOAD_ERROR);
					be.resourceId = this.id;
					throw be;
				}
				return new InputStreamReader(ins, charset);
			}

			public boolean isModified() {
				return autoCheck;
			}
		};
	}

	public boolean isModified(Resource re) {
		return autoCheck;
	}

	public boolean exist(String key) {
		try {
			String path = _path(key);
			if (servletContext.getResource(path) != null || servletContext.getResource(path + ".html") != null || servletContext.getResource(path + ".beetl") != null)
				return true;
		} catch (MalformedURLException e) {
		}
		return false;
	}

	protected String _path(String key) {
		return root + key;
	}

	public void close() {
	}

	public void init(GroupTemplate gt) {
		Map<String, String> resourceMap = gt.getConf().getResourceMap();
		if (this.root == null) {
			if (resourceMap.get("root") != null) {
				setRoot(resourceMap.get("root"));
			} else {
				setRoot("/");
			}
		}

		if (this.charset == null) {
			if (resourceMap.get("charset") != null) {
				setCharset(resourceMap.get("charset"));
			} else {
				setCharset("UTF-8");
			}
		}
		if ("false".equals(resourceMap.get("autoCheck")) || "no".equals(resourceMap.get("autoCheck"))) {
			setAutoCheck(false);
		}

		if (servletContext == null)
			servletContext = Mvcs.getServletContext();
	}

	public String getResourceId(Resource resource, String id) {
		return BeetlUtil.getRelPath(resource.getId(), id);
	}

	public void setRoot(String root) {
		if (root == null)
			root = "/";
		if (!root.startsWith("/"))
			root = "/" + root;
		if (!root.endsWith("/"))
			root += "/";
		this.root = root;
	}

	public String getRoot() {
		return root;
	}

	public void setCharset(String charset) {
		this.charset = Charset.forName(charset);
	}

	public String getCharset() {
		return charset == null ? null : charset.toString();
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setAutoCheck(boolean autoCheck) {
		this.autoCheck = autoCheck;
	}
}
