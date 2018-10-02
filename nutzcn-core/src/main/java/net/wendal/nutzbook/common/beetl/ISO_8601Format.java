package net.wendal.nutzbook.common.beetl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.beetl.core.Format;

public class ISO_8601Format implements Format {

	public Object format(Object data, String pattern) {
		Date date = (Date)data;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return df.format(date);
	}

}
