var chain={
	"default" : {
		"ps" : [
		      "net.wendal.nutzbook.mvc.LogTimeProcessor",
		      //"ioc:processor_cdn", // 如果走ioc,一定不能用单例
		      "net.wendal.nutzbook.mvc.CdnResourceProcessor",
		      "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
		      "org.nutz.mvc.impl.processor.EncodingProcessor",
		      "org.nutz.mvc.impl.processor.ModuleProcessor",
		      "org.nutz.integration.shiro.NutShiroProcessor",
		      "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
		      "org.nutz.mvc.impl.processor.AdaptorProcessor",
		      "org.nutz.mvc.impl.processor.MethodInvokeProcessor",
		      "org.nutz.mvc.impl.processor.ViewProcessor"
		      ],
		"error" : 'org.nutz.mvc.impl.processor.FailProcessor'
	}
};