package net.wendal.nutzbook;

import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@SetupBy(value=MainSetup.class)
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
										   "*anno", "net.wendal.nutzbook",
										   "*tx",
										   "*org.nutz.integration.quartz.QuartzIocLoader",// 关联Quartz
										   "*org.snaker.nutz.SnakerIocLoader", /*"flows"*/ //关联snakerflow
										   })
@Modules(scanPackage=true)
@ChainBy(args="mvc/nutzbook-mvc-chain.js")
@Ok("json:full")
@Fail("jsp:jsp.500")
@Localization(value="msg/", defaultLocalizationKey="zh-CN")
public class MainModule {
}
