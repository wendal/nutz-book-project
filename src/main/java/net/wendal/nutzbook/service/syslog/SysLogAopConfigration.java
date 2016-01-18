package net.wendal.nutzbook.service.syslog;

import java.lang.reflect.Method;

import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import net.wendal.nutzbook.annotation.SLog;
import net.wendal.nutzbook.aop.SimpleAopMaker;

@IocBean(name="$aop_syslog")
public class SysLogAopConfigration extends SimpleAopMaker<SLog> {
	
	@Inject
	protected SysLogService sysLogService;
	
	public MethodInterceptor makeIt(SLog slog, Method method) {
		return new SysLogAopInterceptor(sysLogService, slog, method);
	}

}
