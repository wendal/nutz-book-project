package cn.apiclub.captcha.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.awt.image.BufferedImageOp;
import java.awt.image.FilteredImageSource;

public class ImageUtil {
	public static final void applyFilter(BufferedImage img, BufferedImageOp imageOp) {
		BufferedImageFilter filter = new BufferedImageFilter(imageOp);
		FilteredImageSource src = new FilteredImageSource(img.getSource(), filter);
		Image fImg = Toolkit.getDefaultToolkit().createImage(src);
		Graphics2D g = img.createGraphics();
		g.drawImage(fImg, 0, 0, null, null);
		g.dispose();
	}
}
