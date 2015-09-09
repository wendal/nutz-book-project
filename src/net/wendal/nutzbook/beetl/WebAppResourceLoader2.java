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
 * 
 * @author wendal
 *
 */
public class WebAppResourceLoader2 implements ResourceLoader {
	
	private static final Log log = Logs.get();
	
	protected String root;
	
	protected Charset charset;
	
	protected ServletContext servletContext;

	public Resource getResource(final String key) {
		return new Resource(key, this) {
			
			public Reader openReader() {
				InputStream ins = servletContext.getResourceAsStream(_path(key));
				if (ins == null) {
					log.infof("NOT EXIST KEY=%s PATH=%s", key, _path(key));
					BeetlException be = new BeetlException(BeetlException.TEMPLATE_LOAD_ERROR);
					be.resourceId = this.id;
					throw be;
				}
				return new InputStreamReader(ins, charset);
			}
			
			public boolean isModified() {
				return false;
			}
		};
	}

	public boolean isModified(Resource key) {
		return false;
	}

	public boolean exist(String key) {
		try {
			return servletContext.getResource(_path(key)) != null;
		} catch (MalformedURLException e) {
			return false;
		}
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
		
		if (servletContext == null)
			servletContext = Mvcs.getServletContext();
	}

	public String getResourceId(Resource resource, String id) {
		return BeetlUtil.getRelPath(resource.getId(), id);
	}

	public void setRoot(String root) {
		if (root == null)
			root = "/";
		else if (!root.startsWith("/"))
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
}
