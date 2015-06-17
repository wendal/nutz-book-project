package org.snaker.nutz;

import java.util.Map;

import org.nutz.el.El;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.snaker.engine.Expression;

public class NutzExpression implements Expression {
	
	private static final Log log = Logs.get();
	
	@SuppressWarnings("unchecked")
	public <T> T eval(Class<T> T, String expr, Map<String, Object> args) {
		CharSegment dest = new CharSegment(Strings.trim(expr));
        
		log.debugf("expr=[%s], args=[%s]", expr, Json.toJson(args, JsonFormat.compact().setIgnoreNull(false)));
		Context context = Lang.context();
        // 将每个占位符解析成表达式
		Context ctx = Lang.context(args);
        for (String key : dest.keys()) {
            context.set(key, new El(key).eval(ctx));
        }
        // 生成解析后的结果
        Object re = dest.render(context);
        if (T == String.class) {
        	if (re == null)
        		return (T)"";
        	return (T)re.toString();
        }
        if (T == Boolean.class) {
        	if ("true".equals(re))
        		return (T)Boolean.TRUE;
        	else
        		return (T)Boolean.FALSE;
        }
        return (T)re;
	}
}