package net.wendal.nutzbook.service.syslog;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.wendal.nutzbook.annotation.SLog;

import org.nutz.aop.matcher.SimpleMethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(name="$aop_syslog")
public class SysLogAopConfigration implements AopConfigration {
	
	@Inject
	protected SysLogService sysLogService;

	public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz) {
		List<InterceptorPair> list = new ArrayList<InterceptorPair>();
		for (Method method : clazz.getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
				SLog slog = method.getAnnotation(SLog.class);
				if (slog != null) {
					InterceptorPair ipair = new InterceptorPair(new SysLogAopInterceptor(sysLogService, slog, method), new SimpleMethodMatcher(method));
					list.add(ipair);
				}
			}
		}
		if (list.isEmpty())
			return null;
		return list;
	}

}
