package net.wendal.nutzbook.mvc;

import java.util.ArrayList;
import java.util.List;

import org.nutz.integration.shiro.NutShiroProcessor;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Processor;
import org.nutz.mvc.impl.NutActionChain;
import org.nutz.mvc.impl.processor.ActionFiltersProcessor;
import org.nutz.mvc.impl.processor.AdaptorProcessor;
import org.nutz.mvc.impl.processor.EncodingProcessor;
import org.nutz.mvc.impl.processor.FailProcessor;
import org.nutz.mvc.impl.processor.MethodInvokeProcessor;
import org.nutz.mvc.impl.processor.ModuleProcessor;
import org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor;
import org.nutz.mvc.impl.processor.ViewProcessor;

public class MyActionChainMaker implements ActionChainMaker {

	public ActionChain eval(NutConfig config, ActionInfo ai) {
		List<Processor> list = normalList();
		
		// 首先,在所有请求之前,插入2个
		list.add(0, new LogTimeProcessor());
		list.add(1, new DailyUniqueUsersProcessor());
		
		addBefore(list, ActionFiltersProcessor.class, new NutShiroProcessor());
		
		// 最后是专门负责兜底的异常处理器
		Processor error = new FailProcessor();
		return new NutActionChain(list, error, ai);
	}
	
	protected List<Processor> normalList() {
		List<Processor> list = new ArrayList<>();
		list.add(new UpdateRequestAttributesProcessor());
		list.add(new EncodingProcessor());
		list.add(new ModuleProcessor());
		list.add(new ActionFiltersProcessor());
		list.add(new AdaptorProcessor());
		list.add(new MethodInvokeProcessor());
		list.add(new ViewProcessor());
		return list;
	}
	
	protected List<Processor> addBefore(List<Processor> list, Class<?> klass, Processor processor) {
		for (int i = 0; i < list.size(); i++) {
			if (klass.isAssignableFrom(list.get(i).getClass())) {
				list.add(i, processor);
				return list;
			}
		}
		return list;
	}
}
