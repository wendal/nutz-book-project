package net.wendal.nutzbook;

import org.nutz.boot.NbApp;
import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.UrlMappingBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.plugins.apidoc.ApidocUrlMapping;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.view.pdf.PdfViewMaker;

@Api(name = "NutzCN论坛", description = "The answer to life, the universe and everything",
        match = ApiMatchMode.ONLY)
@SetupBy(value = MainSetup.class)
@IocBy(args = {"*slog"})
@Modules
@ChainBy(args = "mvc/nutzbook-mvc-chain.js")
@Ok("json:full")
@Fail("jsp:jsp.500")
@Views({PdfViewMaker.class})
@UrlMappingBy(ApidocUrlMapping.class)
public class MainLauncher {
    
    public static void main(String[] args) {
        new NbApp().setArgs(args).setPrintProcDoc(true).run();
    }
}
