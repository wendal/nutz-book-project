package org.nutz.aop.interceptor.async;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;

public class AsyncAopIocLoader implements IocLoader{
	
	protected String name = "$aop_async";
	
	protected int size;
	
	public AsyncAopIocLoader(){
		size = 32;
	}
	
	public AsyncAopIocLoader(int size) {
		this.size = size;
	}

	public String[] getName() {
		return new String[]{name};
	}

	public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
		if (!has(name))
			return null;
		IocObject iobj = new IocObject();
		iobj.setType(AsyncAopConfigure.class);
		IocValue ival = new IocValue();
		ival.setValue(size);
		iobj.addArg(ival);
		IocEventSet events = new IocEventSet();
		events.setDepose("close");
		iobj.setEvents(events);
		return iobj;
	}

	public boolean has(String name) {
		return this.name.equals(name);
	}
}
