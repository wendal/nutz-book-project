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
import org.nutz.mvc.annotation.UrlMappingBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.plugins.apidoc.ApidocUrlMapping;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.view.freemarker.FreemarkerViewMaker;
import org.nutz.plugins.view.pdf.PdfViewMaker;

import net.wendal.nutzbook.beetl.BeetlViewMaker2;

@Api(name = "NutzCN论坛", description = "The answer to life, the universe and everything",
        match = ApiMatchMode.ONLY)
@SetupBy(value = MainSetup.class)
@IocBy(args = {"*js",
               "ioc/",
               "*anno",
               "net.wendal.nutzbook",
               "*quartz", // 关联Quartz
               "*async", "128",
               "*tx",
               "*jedis",
               "*slog",
               "*org.nutz.plugins.view.freemarker.FreemarkerIocLoader"})
@Modules(scanPackage = true)
@ChainBy(args = "mvc/nutzbook-mvc-chain.js")
@Ok("json:full")
@Fail("jsp:jsp.500")
@Localization(value = "msg/", defaultLocalizationKey = "zh-CN")
@Views({BeetlViewMaker2.class, FreemarkerViewMaker.class, PdfViewMaker.class})
@SessionBy(ShiroSessionProvider.class)
@UrlMappingBy(ApidocUrlMapping.class)
public class MainModule {}
