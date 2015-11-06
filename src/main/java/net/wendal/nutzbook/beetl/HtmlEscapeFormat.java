package net.wendal.nutzbook.beetl;

import org.beetl.core.Format;
import org.nutz.lang.Strings;

public class HtmlEscapeFormat implements Format {

	public Object format(Object data, String pattern) {
		return Strings.escapeHtml(String.valueOf(data));
	}

	

}
