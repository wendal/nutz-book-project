package net.wendal.nutzbook.beetl;

import net.wendal.nutzbook.util.Markdowns;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class MarkdownFunction implements Function {

	public Object call(Object[] paras, Context ctx) {
		 return Markdowns.toHtml(String.valueOf(paras[0]), null);
	}

}
