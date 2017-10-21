package net.wendal.nutzbook.core.service.impl;

import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.wendal.nutzbook.core.service.JvmJsService;

@IocBean(name="jvmJsService", create="init")
public class JvmJsServiceImpl implements JvmJsService {
    
    private ScriptEngineManager engineManager;
    
    protected ScriptEngine jsScriptEngine;
    
    private static final Log log = Logs.get();
    
    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Override
    public Object invoke(String jsStr, NutMap context, boolean returnException) {
        Bindings bindings = jsScriptEngine.createBindings();
        for (Entry<String, Object> en : context.entrySet()) {
            bindings.put(en.getKey(), en.getValue());
        }
        try {
            jsStr = "function _nutzcn_js(){" + jsStr + "};_nutzcn_js();";
            return ((Compilable) jsScriptEngine).compile(jsStr).eval(bindings);
        }
        catch (ScriptException e) {
            log.debug("js eval fail", e);
            if (returnException)
                return e;
        }
        return null;
    }

    
    public void init() {
        engineManager = new ScriptEngineManager();
        jsScriptEngine = engineManager.getEngineByExtension("js");
    }
}
