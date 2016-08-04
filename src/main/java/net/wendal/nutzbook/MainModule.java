package net.wendal.nutzbook;

import org.nutz.integration.shiro.ShiroSessionProvider;
import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SessionBy;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.plugins.view.freemarker.FreemarkerViewMaker;

import net.wendal.nutzbook.beetl.BeetlViewMaker2;

@SetupBy(value=MainSetup.class)
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
										   "*anno", "net.wendal.nutzbook",
										   "*quartz",// 关联Quartz
										   "*async",
										   "*tx",
										   "*org.nutz.plugins.view.freemarker.FreemarkerIocLoader",
										   "*dubbo", "dubbo-server.xml" // 配置dubbo
										   })
@Modules(scanPackage=true)
@ChainBy(args="mvc/nutzbook-mvc-chain.js")
@Ok("json:full")
@Fail("jsp:jsp.500")
@Localization(value="msg/", defaultLocalizationKey="zh-CN")
@Views({BeetlViewMaker2.class,FreemarkerViewMaker.class})
@SessionBy(ShiroSessionProvider.class)
public class MainModule {}
