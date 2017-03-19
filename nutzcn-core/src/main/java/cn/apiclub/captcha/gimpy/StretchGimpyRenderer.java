package cn.apiclub.captcha.gimpy;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Stretch the given image over the X and Y axes. If no scale is given,
 * defaults to an X scale of 1.0 and a Y scale of 3.0 (i.e. make the image
 * tall but do not affect the width).
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 *
 */
public class StretchGimpyRenderer implements GimpyRenderer {

	private static final double XDEFAULT = 1.0;
	private static final double YDEFAULT = 3.0;
	
	private final double _xScale;
	private final double _yScale;
	
	public StretchGimpyRenderer() {
		this(XDEFAULT, YDEFAULT);
	}
	
	public StretchGimpyRenderer(double xScale, double yScale) {
		_xScale = xScale;
		_yScale = yScale;
	}
	
	@Override
	public void gimp(BufferedImage image) {
		Graphics2D g = image.createGraphics();
		AffineTransform at = new AffineTransform();
		at.scale(_xScale, _yScale);
//		RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawRenderedImage(image, at);
	}
}
