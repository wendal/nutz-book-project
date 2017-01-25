package net.wendal.nutzbook.hotplug;

import org.beetl.core.Resource;
import org.beetl.core.resource.StringTemplateResource;
import org.beetl.core.resource.WebAppResourceLoader;

public class HotPlugResourceLoader extends WebAppResourceLoader {

    public HotPlugResourceLoader() {
        super();
    }

    public HotPlugResourceLoader(String root, String charset) {
        super(root, charset);
    }

    public HotPlugResourceLoader(String root) {
        super(root);
    }

    public Resource getResource(String key) {
        // 从插件里面找找呗
        for (HotPlugConfig hc : HotPlug.plugins.values()) {
            String tmp = key;
            if (tmp.startsWith("/"))
                tmp = tmp.substring(1);
            String tmpl = hc.getTmpls().get(tmp);
            if (tmpl != null) {
                return new StringTemplateResource(tmpl, this);
            }
        }
        return super.getResource(key);
    }
}
