package cn.apiclub.captcha.gimpy;

import java.awt.image.BufferedImage;

import cn.apiclub.captcha.filter.image.ShadowFilter;
import static cn.apiclub.captcha.util.ImageUtil.applyFilter;

/**
 * Adds a dark drop-shadow.
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 * 
 */
public class DropShadowGimpyRenderer implements GimpyRenderer {
	private static final float DEFAULT_RADIUS = 3;
	private static final float DEFAULT_OPACITY = 0.75f;
	
	private final float _radius;
	private final float _opacity;
	
	public DropShadowGimpyRenderer() {
		this(DEFAULT_RADIUS, DEFAULT_OPACITY);
	}
	
	public DropShadowGimpyRenderer(float radius, float opacity) {
		_radius = radius;
		_opacity = opacity;
	}

	@Override
    public void gimp(BufferedImage image) {
        ShadowFilter sFilter = new ShadowFilter();
        sFilter.setRadius(_radius);
        sFilter.setOpacity(_opacity);
        applyFilter(image, sFilter);
    }
}