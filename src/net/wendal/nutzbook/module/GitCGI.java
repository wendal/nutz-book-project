package net.wendal.nutzbook.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.VoidAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;


/**
 * Git的Http-backend封装
 */
@IocBean(create="init")
public class GitCGI {

	private static final Log log = Logs.get();
	String GIT_PROJECT_ROOT = "H:\\git\\";
	@Inject GitAdminModule gitAdminModule;
	protected static boolean DEBUG = false;
	
	public void init() {
		GIT_PROJECT_ROOT = gitAdminModule.root.getAbsolutePath();
	}

	// TODO 权限管理
	@AdaptBy(type = VoidAdaptor.class)
	@Ok("void")
	@At("/git/?/?/*")
	@Fail("http:503")
	public void cgi(String user, String repo) throws IOException {
		HttpServletRequest req = Mvcs.getReq();
		HttpServletResponse res = Mvcs.getResp();

		// 生成git库下的相对路径
		List<String> paths = new ArrayList<String>(Mvcs.getActionContext().getPathArgs());
		paths.remove(0);
		paths.remove(0);
		String path = "/" + Strings.join("/", paths.toArray());
		// 启动git http-backend
		Process p = Runtime.getRuntime().exec(
				new String[] { "git", "http-backend" },
				buildEnv(req, GIT_PROJECT_ROOT + "/" + user + "/" +  repo, path));
		// 将客户端的post/put传入的流传给git-http-backend
		Streams.write(p.getOutputStream(), req.getInputStream());
		// 读取响应, 格式与标准的Http响应就相差个响应行
		writeBack(p, res);
	}
	
	protected void writeBack(Process p, HttpServletResponse res) {
		InputStream inFromCgi = p.getInputStream();
		String line = "";
		OutputStream os = null;
		try {
			while ((line = getTextLineFromStream(inFromCgi)).length() > 0) {
				if (!line.startsWith("HTTP")) {
					int k = line.indexOf(':');
					if (k > 0) {
						String key = line.substring(0, k).trim();
						String value = line.substring(k + 1).trim();
						if ("Location".equals(key)) {
							res.sendRedirect(res.encodeRedirectURL(value));
						} else if ("Status".equals(key)) {
							String[] token = value.split(" ");
							int status = Integer.parseInt(token[0]);
							res.setStatus(status);
						} else {
							// add remaining header items to our response header
							res.addHeader(key, value);
						}
					}
				}
			}
			// 响应体就写入客户端咯
			os = res.getOutputStream();
			Streams.write(os, inFromCgi);
			// 打印出错信息
			// System.out.println(new
			// String(Streams.readBytes(p.getErrorStream())));
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Streams.safeClose(os);
		}
	}

	public static String[] buildEnv(HttpServletRequest req, String root, String path) {
		try {
			root = new File(root).getCanonicalPath();
		} catch (IOException e) {
		}
		log.debug("git repo root=" + root);
		// 逐一构建CGI所需要的一堆环境变量
		NutMap env = new NutMap();

		env.setv("AUTH_TYPE", req.getAuthType());
		env.setv("CONTENT_LENGTH", Integer.toString(req.getContentLength()));
		env.setv("CONTENT_TYPE", req.getContentType());
		env.setv("GATEWAY_INTERFACE", "CGI/1.1");

		env.setv("PATH_INFO", path);

		// env.setv("PATH_TRANSLATED", req.getPathTranslated());
		env.setv("QUERY_STRING", req.getQueryString());
		env.setv("REMOTE_ADDR", req.getRemoteAddr());
		env.setv("REMOTE_HOST", req.getRemoteHost());
		// The identity information reported about the connection by a
		// RFC 1413 [11] request to the remote agent, if
		// available. Servers MAY choose not to support this feature, or
		// not to request the data for efficiency reasons.
		// "REMOTE_IDENT" => "NYI"
		env.setv("REMOTE_USER", req.getRemoteUser());
		env.setv("REQUEST_METHOD", req.getMethod());
		// env.set("SCRIPT_NAME", scriptName);
		// env.set("SCRIPT_FILENAME", scriptPath);
		// env.set("SERVER_NAME", req.getServerName());
		// env.set("SERVER_PORT", Integer.toString(req.getServerPort()));
		env.setv("SERVER_PROTOCOL", req.getProtocol());
		// env.set("SERVER_SOFTWARE", getServletContext().getServerInfo());

		Enumeration<String> enm = req.getHeaderNames();
		while (enm.hasMoreElements()) {
			String name = (String) enm.nextElement();
			String value = req.getHeader(name);
			env.setv(
					"HTTP_"
							+ name.toUpperCase(Locale.ENGLISH)
									.replace('-', '_'), value);
		}

		// GIT 所需要的环境变量
		env.setv("GIT_PROJECT_ROOT", root);
		env.setv("GIT_HTTP_EXPORT_ALL", "1");

		String[] _env = new String[env.size()];
		List<String> keys = new ArrayList<String>(env.keySet());
		for (int i = 0; i < _env.length; i++) {
			_env[i] = keys.get(i) + "=" + env.getString(keys.get(i), "");
			if (DEBUG)
				log.debug("ENV: " + _env[i]);
		}
		return _env;
	}

	public static String getTextLineFromStream(InputStream is)
			throws IOException {
		StringBuilder buffer = new StringBuilder();
		int b;

		while ((b = is.read()) != -1 && b != '\n') {
			buffer.append((char) b);
		}
		if (DEBUG)
			log.debug(">>" + buffer.toString().trim());
		return buffer.toString().trim();
	}
}
