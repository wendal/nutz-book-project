package net.wendal.nutzbook.module;

import net.wendal.nutzbook.service.EmailService;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;

public abstract class BaseModule {
	
	/** 注入同名的一个ioc对象 */
	@Inject protected Dao dao;

	@Inject protected EmailService emailService;
}
