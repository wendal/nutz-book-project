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
				},
				'perm_chow' : {
					refer : 'permissionShiro'
				},
				'cms_perm' : {
					refer : 'permission'
				},
				'currentTime' : {
					refer : 'currentTime'
				},
				'timeFormat' : {
					refer : 'timeFormat'
				},
				'ioc' : {
					refer : '$ioc'
				},
				'conf' : {
					java : '$conf.toMap()'
				},
				'cdnbase' : {
					java : "$conf.get('cdn.urlbase')"
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
	freemarkerDirectiveFactory : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirectiveFactory",
		fields : {
			freemarker : 'custom/freemarker.properties',
		}
	}
};