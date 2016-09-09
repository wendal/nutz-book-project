package net.wendal.nutzbook.module;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
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

@Api(name="二维码生成", description="生成二维码")
@IocBean
@At("/qrcode")
public class QrcodeModule extends BaseModule {
	
	private static final Log log = Logs.get();
	
	protected MultiFormatWriter writer = new MultiFormatWriter();

	@At
	@GET
	@Ok("raw:png") // TODO 改进为内嵌logo如何
	public Object get(@Param("data")String data, @Param("w")int w, @Param("h")int h) {
		if (Strings.isBlank(data))
			return new HttpStatusView(404);
		// 修正长宽
		if (w < 1)
			w = 256;
		else if (w > 1024)
			w = 1024;
		if (h < 1)
			h = 256;
		else if (h > 1024)
			h = 1024;
		try {
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, w, h, hints);
			return MatrixToImageWriter.toBufferedImage(matrix);
		} catch (WriterException e) {
			// 生成失败,一般是文本太长,指定的尺寸放不下
			log.debug("qrcode write fail", e);
			return new HttpStatusView(500);
		}
	}
	
}
