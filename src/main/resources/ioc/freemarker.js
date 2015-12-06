var ioc = {
	shiroTags : {
		type : "net.wendal.nutzbook.shiro.freemarker.ShiroTags",
		singleton : true
	},
	permissionResolver : {
		type : "org.apache.shiro.authz.permission.WildcardPermissionResolver"
	},
	configuration : {
		type : "freemarker.template.Configuration"
	},
	freeMarkerConfigurer : {
		type : "org.nutz.plugins.view.freemarker.FreeMarkerConfigurer",
		events : {
			create : 'init'
		},
		fields : {
			tags : {
				'shiro' : {
					refer : 'shiroTags'
				}
			}
		},
		args : [ {
			refer : "configuration"
		}, {
			app : '$servlet'
		}, "WEB-INF", ".ftl", {
			refer : "freemarkerDirectiveFactory"
		} ]
	},
	permissionShiro : {
		type : "net.wendal.nutzbook.freemarker.PermissionShiroFreemarker",
		args : [ {
			refer : "permissionResolver"
		}, {
			refer : "dao"
		} ]
	},
	permission : {
		type : "net.wendal.nutzbook.freemarker.PermissionDirective"
	},
	currentTime : {
		type : "net.wendal.nutzbook.freemarker.CurrentTimeDirective"
	},
	timeFormat : {
		type : "net.wendal.nutzbook.freemarker.TimeFormatDirective"
	},
	permissionShiroDirective : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirective",
		args : [ "perm_chow", {
			refer : "permissionShiro"
		} ]
	},
	permissionDirective : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirective",
		args : [ "cms_perm", {
			refer : "permission"
		} ]
	},
	currentTimeDirective : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirective",
		args : [ "currentTime", {
			refer : "currentTime"
		} ]
	},
	timeFormatDirective : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirective",
		args : [ "timeFormat", {
			refer : "timeFormat"
		} ]
	},
	freemarkerDirectiveFactory : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirectiveFactory",
		events : {
			create : 'init'
		},
		fields : {
			freemarker : 'custom/freemarker.properties',
		},
		args : [ {
			refer : "permissionDirective"
		}, {
			refer : "timeFormatDirective"
		}, {
			refer : "currentTimeDirective"
		}, {
			refer : "permissionShiroDirective"
		} ]
	}
};