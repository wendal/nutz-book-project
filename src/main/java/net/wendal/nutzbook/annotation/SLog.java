package net.wendal.nutzbook.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SLog {

	String tag();
	
	String msg();
	
	boolean before() default false;
	
	boolean after() default true;
	
	boolean error() default true;
	
	boolean async() default true;
}
