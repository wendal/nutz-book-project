var ioc = {
		dynamicFormService : {
			type : "net.wendal.nutzbook.service.impl.OrmDynamicFormService",
			fields : {
				dao : {refer:"dao"}
			}
		}
};