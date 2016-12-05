package net.wendal.nutzbook.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SPlugin {

    /**
     * 写个简单描述呗
     * @return
     */
    String value();
    /**
     * 需要匹配的类名的正则表达式
     * @return
     */
    String clazz() default "";
    /**
     * 需要匹配的方法名的正则表达式
     * @return
     */
    String method() default "";
}
