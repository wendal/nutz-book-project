package net.wendal.nutzbook.module.qqbot;

import net.wendal.nutzbook.bean.qqbot.QQBotMessage;
import net.wendal.nutzbook.bean.qqbot.QQBotRole;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.tmpl.Tmpl;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

/**
 * Created by wendal on 2015/12/16.
 */
public class DefaultQQBotExecutor implements QQBotExecutor {

    private static final Log log = Logs.get();

    @SuppressWarnings("unchecked")
	public String execute(QQBotMessage message, QQBotRole role) throws Exception {
        if (Strings.isBlank(message.Message))
            return "";
        log.debug(message.toString());
        if ("class".equals(role.matchType)) {
            Class<? extends QQBotExecutor> klass = (Class<? extends QQBotExecutor>) Class.forName(role.matchValue);
            if (klass.getAnnotation(IocBean.class) != null)
                return Mvcs.getIoc().get(klass).execute(message, role);
            return klass.newInstance().execute(message, role);
        }
        if ("ioc".equals(role.matchType)) {
            return Mvcs.getIoc().get(QQBotExecutor.class, role.matchValue).execute(message, role);
        }
        boolean matched = false;
        switch (role.matchType) {
            case "match":
                matched = Lang.equals(role.matchValue, message.Message);
                break;
            case "prefix":
                matched = message.Message.startsWith(role.matchValue);
                break;
            case "patten":
                matched = message.Message.matches(role.matchValue);
                break;
        }
        if (!matched)
            return null;
        switch (role.executeType) {
            case "string":
                return role.executeBody;
            case "tmpl" :
                return Tmpl.exec(role.executeBody, message.toNutBean());
            case "ignore" :
                return "";
            case "js":
                ScriptEngineManager sem = new ScriptEngineManager();
                ScriptEngine engine = sem.getEngineByExtension("js");
                SimpleScriptContext ctxt = new SimpleScriptContext();
                ctxt.setAttribute("ioc", Mvcs.getIoc(), ScriptContext.ENGINE_SCOPE);
                ctxt.setAttribute("message", message, ScriptContext.ENGINE_SCOPE);
                ctxt.setAttribute("role", role, ScriptContext.ENGINE_SCOPE);
                Object result = engine.eval("function _qqbot(){"+ role.executeBody + "};_qqbot();", ctxt);
                return result == null ? null : result.toString();
        }
        return null;
    }

}
