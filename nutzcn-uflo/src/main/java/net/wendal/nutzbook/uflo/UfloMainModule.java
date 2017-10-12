package net.wendal.nutzbook.uflo;

import org.nutz.mvc.annotation.IocBy;

@IocBy(args={"*anno", "net.wendal.nutzbook.uflo", "*spring", "classpath*:spring-context.xml"})
public class UfloMainModule {

}
