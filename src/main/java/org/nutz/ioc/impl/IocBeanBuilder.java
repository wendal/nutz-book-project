package org.nutz.ioc.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 一个IocBean的辅助类
 * @author wendal
 *
 */
public class IocBeanBuilder {
	
	private static final Log log = Logs.get();

	/**
	 * 将一个对象放入Ioc容器的上下文中
	 * @param ioc Ioc容器,必须实现Ioc2接口, NutIoc实现了这个接口
	 * @param name 将要
	 * @param obj
	 */
	public static void add(Ioc ioc, String name, Object obj) {
		Ioc2 _ioc = (Ioc2)ioc;
		if (_ioc.has(name)) {
			log.info("bean name=%s is exist, will override it");
		}
		_ioc.getIocContext().save("app", name, new ObjectProxy(obj));
	}

	public static void asFactory(Ioc ioc, Object obj) {
		if (obj == null) {
			log.warn("obj is null");
			return;
		}
		for(Method method : obj.getClass().getMethods()) {
			// 必须是getXXX形式
			String methodName = method.getName();
			if (!methodName.startsWith("get") || methodName.length() < 4)
				continue;
			// 必须带@IocBean注解
			IocBean iocBean = method.getAnnotation(IocBean.class);
			if (iocBean == null)
				continue;
			// 检查参数
			List<Object> args = new ArrayList<Object>();
			for (Class<?> klass : method.getParameterTypes()) {
				if (Ioc.class.isAssignableFrom(klass)) {
					args.add(ioc);
				} else if (klass.getAnnotation(Inject.class) != null) {
					Inject inject = klass.getAnnotation(Inject.class);
					String inj = inject.value();
					if (Strings.isBlank(inj)) {
						throw new IllegalArgumentException("require @Inject('xxxx')");
					}
					if (inj.contains(":") && !inj.startsWith("refer:")) {
						throw new IllegalArgumentException("only support @Inject('refer:xxxx') yet");
					}
					if (inj.contains(":")) {
						inj = inj.substring(inj.indexOf(":")+1);
					}
					args.add(ioc.get(klass, inj));
				} else {
					args.add(ioc.get(klass));
				}
			}
			
			try {
				Object bean = method.invoke(obj, args.toArray());
				String name = iocBean.name();
				if (Strings.isBlank(name))
					name = Strings.lowerFirst(methodName.substring(3));
				log.debugf("add bean[name=%s] to ioc, type=[%s]", name, bean.getClass().getName());
				add(ioc, name, bean);
			} catch (Exception e) {
				throw new IllegalArgumentException("fail to create ioc bean using " + method);
			}
			
		}
	}
}
