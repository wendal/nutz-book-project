package net.wendal.nutzbook;

import java.lang.reflect.Method;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.wendal.nutzbook.plugin.AbstractPlugin;
import net.wendal.nutzbook.plugin.SPlugin;

@SPlugin(value="大鲨鱼插件", method="list")
public class SelaPlug extends AbstractPlugin {

    private static final Log log = Logs.get();
    
    @Override
    public boolean beforeInvoke(Object obj, Method method, Object... args) {
        log.debug("大鲨鱼你好...");
        return super.beforeInvoke(obj, method, args);
    }
    
    @Override
    public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
        log.debug("大鲨鱼再见...");
        return super.afterInvoke(obj, returnObj, method, args);
    }
}
