package net.wendal.nutzbook.service.syslog;

import java.lang.reflect.Method;
import java.util.Map;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.el.El;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Mvcs;

import net.wendal.nutzbook.annotation.SLog;
import net.wendal.nutzbook.bean.SysLog;
import net.wendal.nutzbook.util.Toolkit;

public class SysLogAopInterceptor implements MethodInterceptor {
	
	protected SysLogService sysLogService;
	
	protected String source;
	
	protected String tag;
	protected CharSegment before;
	protected CharSegment after;
	protected CharSegment error;
	protected boolean async;
	protected Map<String, El> els;
	protected Ioc ioc;
	
	public SysLogAopInterceptor(Ioc ioc, SLog slog, Method method) {
        if (!Strings.isBlank(slog.before())) {
            before = new CharSegment(slog.before());
            before.keys().forEach((key)->els.put(key, new El(key)));
        }
        if (!Strings.isBlank(slog.after())) {
            after = new CharSegment(slog.after());
            after.keys().forEach((key)->els.put(key, new El(key)));
        }
        if (!Strings.isBlank(slog.error())) {
            error = new CharSegment(slog.error());
            error.keys().forEach((key)->els.put(key, new El(key)));
        }
		this.ioc = ioc;
		this.source = method.getDeclaringClass().getName() + "#" + method.getName();
		this.tag = slog.tag();
		SLog _s = method.getDeclaringClass().getAnnotation(SLog.class);
		if (_s != null) {
			this.tag = _s.tag() + "," + this.tag;
		}
		this.async = slog.async();
	}

	public void filter(InterceptorChain chain) throws Throwable {
		if (before != null)
			doLog("aop.before", before,  chain, null);
		try {
			chain.doChain();
			if (after != null)
				doLog("aop.after", after,  chain, null);
		} catch (Throwable e) {
			if (error != null) 
				doLog("aop.error", error, chain, e);
			throw e;
		}
	}

	protected void doLog(String t, CharSegment seg, InterceptorChain chain, Throwable e) {
		String _msg = null;
		if (seg.hasKey()) {
			Context ctx = Lang.context();
			ctx.set("args", chain.getArgs());
			ctx.set("return", chain.getReturn());
			ctx.set("req", Mvcs.getReq());
			ctx.set("resp", Mvcs.getResp());
			Context _ctx = Lang.context();
			for (String key :seg.keys()) {
				_ctx.set(key, els.get(key).eval(ctx));
			}
			_msg = seg.render(_ctx).toString();
		} else {
			_msg = seg.getOrginalString();
		}
		SysLog sysLog = SysLog.c(t, tag, source, Toolkit.uid(), _msg);
		if (sysLogService == null)
		    sysLogService = ioc.get(SysLogService.class);
		if (async)
			sysLogService.async(sysLog);
		else
			sysLogService.sync(sysLog);
	}
}
