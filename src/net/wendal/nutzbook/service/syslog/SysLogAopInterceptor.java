package net.wendal.nutzbook.service.syslog;

import java.lang.reflect.Method;

import net.wendal.nutzbook.annotation.SLog;
import net.wendal.nutzbook.bean.SysLog;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

public class SysLogAopInterceptor implements MethodInterceptor {
	
	protected SysLogService sysLogService;
	
	protected String source;
	
	protected String tag;
	protected String msg;
	protected boolean before;
	protected boolean after;
	protected boolean error;
	protected boolean async;
	
	public SysLogAopInterceptor(SysLogService sysLogService, SLog slog, Method method) {
		this.sysLogService = sysLogService;
		this.source = method.getDeclaringClass().getName() + "#" + method.getName();
		this.tag = slog.tag();
		SLog _s = method.getDeclaringClass().getAnnotation(SLog.class);
		if (_s != null) {
			this.tag = _s.tag() + "," + this.tag;
		}
		this.msg = slog.msg();
		this.async = slog.async();
		this.before = slog.before();
		this.after = slog.after();
		this.error = slog.error();
	}

	public void filter(InterceptorChain chain) throws Throwable {
		if (before)
			doLog("aop.before", chain, null);
		try {
			chain.doChain();
			if (after)
				doLog("aop.after", chain, null);
		} catch (Throwable e) {
			if (error) 
				doLog("aop.after", chain, e);
			throw e;
		}
	}

	protected void doLog(String t, InterceptorChain chain, Throwable e) {
		SysLog sysLog = SysLog.c(t, tag, source, 0, msg);
		if (async)
			sysLogService.async(sysLog);
		else
			sysLogService.sync(sysLog);
	}
}
