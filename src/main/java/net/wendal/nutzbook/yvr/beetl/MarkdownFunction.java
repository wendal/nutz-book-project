package net.wendal.nutzbook.yvr.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;

import net.wendal.nutzbook.yvr.util.Markdowns;

public class MarkdownFunction implements Function {
    
    protected PropertiesProxy conf;
    
    public MarkdownFunction(PropertiesProxy conf) {
        this.conf = conf;
    }
	
	public String cdnbase(){
        if (conf.getBoolean("cdn.enable", false) && !Strings.isBlank(conf.get("cdn.urlbase"))) {
            return conf.get("cdn.urlbase");
        }
        return null;
	};

	public Object call(Object[] paras, Context ctx) {
		 return Markdowns.toHtml(String.valueOf(paras[0]), cdnbase());
	}

}
