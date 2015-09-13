package net.wendal.nutzbook.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.pegdown.PegDownProcessor;

public class MarkdownFunction implements Function {

	public Object call(Object[] paras, Context ctx) {
		 PegDownProcessor processor = new PegDownProcessor();
		 return processor.markdownToHtml((String) paras[0]);
	}

}
