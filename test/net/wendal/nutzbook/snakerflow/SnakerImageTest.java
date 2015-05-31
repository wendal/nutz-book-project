package net.wendal.nutzbook.snakerflow;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.nutz.img.Images;

public class SnakerImageTest {
	

	@Test
	public void test() throws Throwable {
		SnakerImage s = new SnakerImage();
		InputStream ins = getClass().getResourceAsStream("borrow.xml");
		BufferedImage image = s.render(ins);
		if (image != null)
			Images.write(image, new File("borrow.png"));
	}

}
