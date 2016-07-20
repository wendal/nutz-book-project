package net.wendal.nutzbook.service.syslog;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import net.wendal.nutzbook.annotation.SLog;

@IocBean(name="$aop_syslog")
public class SysLogAopConfigration extends SimpleAopMaker<SLog> {
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	public List<? extends MethodInterceptor> makeIt(SLog slog, Method method, Ioc ioc) {
		return Arrays.asList(new SysLogAopInterceptor(ioc, slog, method));
	}

	public String[] getName() {
		return new String[0];
	}
	
	public boolean has(String name) {
		return false;
	}
}
