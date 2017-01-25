package net.wendal.nutzbook.hotplug;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.impl.ActionInvoker;

public class UrlMappingProxy implements UrlMapping {
    
    private static final Log log = Logs.get();
    
    protected UrlMapping main;

    protected LinkedHashMap<String, UrlMapping> plugins = new LinkedHashMap<>();
    
    public UrlMappingProxy(UrlMapping main) {
        this.main = main;
    }

    public void add(ActionChainMaker maker, ActionInfo ai, NutConfig config) {
        main.add(maker, ai, config);
    }

    public ActionInvoker get(ActionContext ac) {
        for (Entry<String, UrlMapping> en : plugins.entrySet()) {
            String key = en.getKey();
            UrlMapping mapping = en.getValue();
            if (mapping != null) {
                ActionInvoker ai = mapping.get(ac);
                if (ai != null) {
                    log.debugf("found mapping at plugin(%s)", key);
                    return ai;
                }
            }
        }
        return main.get(ac);
    }
    public void add(String path, ActionInvoker invoker) {
        main.add(path, invoker);
    }

    public void add(HotPlugConfig hc) {
        plugins.put(hc.getName(), hc.urlMapping);
    }
    
    public void remove(String key) {
        plugins.remove(key);
    }

}