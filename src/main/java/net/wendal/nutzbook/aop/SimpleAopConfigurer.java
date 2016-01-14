package net.wendal.nutzbook.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.matcher.SimpleMethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

public abstract class SimpleAopConfigurer<T extends Annotation> implements IocLoader, AopConfigration {

	protected Class<T> annoClass;

	public String _name() {
		return Strings.lowerFirst(_anno().getSimpleName());
	}

	public Class<T> _anno() {
		return annoClass;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SimpleAopConfigurer() {
		annoClass = (Class<T>) (Class)Mirror.getTypeParam(getClass(), 0);
	}

	public abstract MethodInterceptor makeIt(T t, Method method);

	@Override
	public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> klass) {
		if (klass.isInterface()
	            || klass.isArray()
	            || klass.isEnum()
	            || klass.isPrimitive()
	            || klass.isMemberClass()
	            || klass.isAnnotation()
	            || klass.isAnonymousClass())
			return null;
		List<InterceptorPair> list = new ArrayList<>();
		for (Method method : klass.getDeclaredMethods()) {
			int mod = method.getModifiers();
            if (mod == 0 || Modifier.isStatic(mod) || Modifier.isPrivate(mod) 
                    || Modifier.isFinal(mod)
                    || Modifier.isAbstract(mod))
                continue;
			T t = method.getAnnotation(_anno());
			if (t != null) {
				MethodInterceptor mi = makeIt(t, method);
				if (mi != null) {
					list.add(new InterceptorPair(mi, new SimpleMethodMatcher(method)));
				}
			}
		}
		if (list.isEmpty())
			return null;
		return list;
	}

	public String[] getName() {
		return new String[] { "$aop_" + _name() };
	}

	public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
		IocObject iobj = new IocObject();
		iobj.setType(getClass());
		IocEventSet events = new IocEventSet();
		events.setDepose("depose");
		events.setCreate("init");
		events.setFetch("fetch");
		iobj.setEvents(events);
		return iobj;
	}

	public boolean has(String name) {
		return _name().equals(name);
	}
	
	public void init() throws Exception {}
	public void fetch() throws Exception {}
	public void depose() throws Exception{}
}
