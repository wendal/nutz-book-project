package net.wendal.nutzbook.module.yvr;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.mvc.View;
import org.nutz.mvc.adaptor.VoidAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.ForwardView;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.mvc.view.ServerRedirectView;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.repo.Base64;

import net.wendal.nutzbook.module.BaseModule;

/**
 * 取代原nutz.cn的短地址服务
 * @author wendal
 *
 */
@Api(name="短地址服务", description="取代原nutz.cn的短地址服务")
@At("/s")
@IocBean(create="init")
@Ok("raw")
public class YvrShortModule extends BaseModule {
	
	protected AtomicLong gen = new AtomicLong();
	
	@Inject("java:$conf.get('shortit.root')")
	protected String root;
	
	@Inject 
	protected Dao dao;

	@At("/c/?")
	public Object code(String code) {
		long id = Helper.string2Id(code);
		if (id < 0) {
			return 404;
		}
		return render(id);
	}

//	@At("/api/create/url")
//	public Object createUrl(@Param("data") String url) {
//		if (url == null) 
//			return Helper._fail("err.data_emtry");
//		if (url.length() > 1024*4)
//			return Helper._fail("err.url_too_big");
//		return Helper._ok(Helper.createUrl(url));
//	}
	


	@At("/api/create/txt")
	@AdaptBy(type = VoidAdaptor.class)
	public Object createTxt(HttpServletRequest req) throws IOException {
		int fileSize = req.getContentLength();
		if (fileSize < 1)
			return Helper._fail("err.data_emtry");
		if (fileSize > 1024 * 1024 * 10)
			return Helper._fail("err.file_too_big");
		String re = Helper._ok(write(req.getInputStream(), "txt:"));
		return re;
	}

	@At("/api/create/file")
	@AdaptBy(type = VoidAdaptor.class)
	public Object createFile(HttpServletRequest req) throws IOException {
		int fileSize = req.getContentLength();
		if (fileSize < 1)
			return Helper._fail("err.data_emtry");
		if (fileSize > 1024 * 1024 * 10)
			return Helper._fail("err.file_too_big");
		String fileName = req.getHeader("X-File-Name");
		if (Strings.isBlank(fileName))
			fileName = "file.bin";
		else {
			fileName = new String(Base64.decodeFast(fileName), "UTF8");
		}
		return Helper._ok(write(req.getInputStream(), "bin:"+fileName));
	}

	@At("/api/read/?")
	public Object read(String code, HttpServletResponse resp)
			throws FileNotFoundException {
		long id = Helper.string2Id(code);
		if (id < 0)
			return HttpStatusView.HTTP_404;
		File f = new File(root + "/" + idPath(id));
		if (!f.exists())
			return HttpStatusView.HTTP_404;
		if (resp != null) {
			resp.setHeader("Content-Length", "" + f.length());
			resp.setContentType("text/plain; charset=utf8");
		}
		return new FileInputStream(f);
	}

	@At("/api/down/?")
	@Ok("void")
	public Object down(String code, HttpServletResponse resp)
			throws IOException {
		long id = Helper.string2Id(code);
		if (id < 0)
			return HttpStatusView.HTTP_404;
		File f = new File(root + "/" + idPath(id));
		String meta = meta(f);
		if (meta == null || !meta.startsWith("bin:") || meta.length() < 5) {
			return HttpStatusView.HTTP_404;
		}

		String filename = meta.substring(4);
		filename = URLEncoder.encode(filename, Encoding.UTF8);

		resp.setHeader("Content-Length", "" + f.length());
		resp.setHeader("Content-Disposition", "attachment; filename=\""
				+ filename + "\"");
		Streams.writeAndClose(resp.getOutputStream(), Streams.fileIn(f));
		return null;
	}
	
	@At("/api/last")
	public String lastUrl() {
		return ""+gen.get();
	}

	public View render(long id) {
		File f = new File(root + "/" + idPath(id));
		String metaStr = meta(f);
		if (metaStr == null) {
			return HttpStatusView.HTTP_404;
		}
		if (metaStr.startsWith("url:")) {
			return new ServerRedirectView(Files.read(f));
		} else if (metaStr.startsWith("txt:")) {
			return new ForwardView("/s/txt.html");
		} else {
			return new ForwardView("/s/down.html");
		}
	}
	
	public String meta(File f) {
		if (f == null)
			return null;
		File meta = new File(f.getParentFile(), f.getName() + ".meta");
		if (!meta.exists() || meta.length() == 0)
			return null;
		return Files.read(meta);
	}
	
	public long write(InputStream ins, String meta) {
		long id = next();
		String path = idPath(id);
		Files.write(root + "/" + path, ins);
		Files.write(root + "/" + path + ".meta", meta);
		return id;
	}
	
	@Aop("redis")
	protected long next() {
		long id = gen.getAndIncrement();
		jedis().hset("ids", "shortit", ""+id);
		return id;
	}
	
	public static String idPath(long id) {
		String tmp = String.format("%016X", id);
		String path = tmp.substring(0, 2) + "/" + 
					  tmp.substring(2,4) + "/" + 
					  tmp.substring(4, 6) + "/" + 
					  tmp.substring(6, 8) + "/" +
					  tmp.substring(10, 12) + "/" +
					  tmp.substring(12, 14) + "/" +
					  tmp.substring(14);
		return path;
	}

	@Aop("redis")
	public void init() {
		String _id = jedis().hget("ids", "shortit");
		if (_id != null) {
			long id = Long.parseLong(_id);
			this.gen.set(id);
		}
	}
}
