package net.wendal.nutzbook.module;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import org.nutz.plugins.apidoc.annotation.Api;
import net.wendal.nutzbook.util.Toolkit;

@Api(name="跨屏登陆", description="用于跨屏免登陆")
@IocBean
@At("/cs")
public class CrossScreenModule {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected QrcodeModule qrcodeModule;
	
	protected MultiFormatWriter writer = new MultiFormatWriter();

	@At("/qr")
	@Ok("raw:png")
	public Object crossScreen(@Param("url")String url,
							  HttpServletRequest req,
							  @Param("w")int w, @Param("h")int h) {
		if (Strings.isBlank(url)) {
			return new HttpStatusView(404);
		}
		NutMap map = new NutMap();
		map.put("url", url);
		map.put("t", System.currentTimeMillis());
		Integer uid = Toolkit.uid();
		if (uid != null) {
			map.put("uid", uid);
		}
		String json = Json.toJson(map, JsonFormat.compact());
		log.debug("token json = " + json);
		String token = Toolkit._3DES_encode(Toolkit.csKEY, json.getBytes());
		String tmp = req.getRequestURL().toString();
		String pass = tmp.substring(0, tmp.length() - 2) + "pass";
		String URL = pass  + "?token=" +token;
		log.debug("token URL=" + URL);
		try {
			// 修正长宽
			if (w < 1)
				w = 256;
			else if (w > 1024)
				w = 1024;
			if (h < 1)
				h = 256;
			else if (h > 1024)
				h = 1024;
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix matrix = writer.encode(URL, BarcodeFormat.QR_CODE, w, h, hints);
			return MatrixToImageWriter.toBufferedImage(matrix);
		} catch (WriterException e) {
			// 生成失败,一般是文本太长,指定的尺寸放不下
			log.debug("qrcode write fail", e);
			return new HttpStatusView(500);
		}
	}
}
