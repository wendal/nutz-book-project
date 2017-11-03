package net.wendal.nutzbook.core.module;

import java.awt.image.BufferedImage;

import javax.servlet.http.HttpSession;

import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import net.wendal.nutzbook.common.util.Toolkit;

@Api(name="验证码", description="公共验证码")
@IocBean
@At("/captcha")
public class CaptchaModule {

	@At
	@Ok("raw:png")
	public BufferedImage next(HttpSession session, @Param("w") int w, @Param("h") int h) {
		if (w * h < 1) { //长或宽为0?重置为默认长宽.
			w = 145;
			h = 35;
		}
		String text = R.captchaChar(4);
        session.setAttribute(Toolkit.captcha_attr, text);
		return Images.createCaptcha(text, w, h, null, "FFF", null);
	}
}
