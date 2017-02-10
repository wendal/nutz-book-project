package net.wendal.nutzbook.common.beetl;

import net.wendal.nutzbook.common.util.Markdowns;
import net.wendal.nutzbook.core.CoreMainSetup;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.nutz.lang.Strings;

public class MarkdownFunction implements Function {
	
	public static String cdnbase(){
        if (CoreMainSetup.conf.getBoolean("cdn.enable", false) && !Strings.isBlank(CoreMainSetup.conf.get("cdn.urlbase"))) {
            return CoreMainSetup.conf.get("cdn.urlbase");
        }
        return null;
	};

	public Object call(Object[] paras, Context ctx) {
		 return Markdowns.toHtml(String.valueOf(paras[0]), cdnbase());
	}

}
