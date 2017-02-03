package net.wendal.nutzbook.common.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class StringSubMaxFunction implements Function {

	public Object call(Object[] paras, Context ctx) {
		String cnt = (String) paras[0];
		int max = ((Number)paras[1]).intValue();
		if (cnt.length() <= max)
			return cnt;
		return cnt.substring(0, max);
	}

}
