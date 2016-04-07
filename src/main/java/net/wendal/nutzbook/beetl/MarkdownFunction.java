package net.wendal.nutzbook.beetl;

import net.wendal.nutzbook.MainSetup;
import net.wendal.nutzbook.util.Markdowns;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.nutz.lang.Strings;

public class MarkdownFunction implements Function {
	
	public static String cdnbase(){
        if (MainSetup.conf.getBoolean("cdn.enable", false) && !Strings.isBlank(MainSetup.conf.get("cdn.urlbase"))) {
            return MainSetup.conf.get("cdn.urlbase");
        }
        return null;
	};

	public Object call(Object[] paras, Context ctx) {
		 return Markdowns.toHtml(String.valueOf(paras[0]), cdnbase());
	}

}
