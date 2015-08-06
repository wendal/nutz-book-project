package net.wendal.nutzbook.service;

import static net.wendal.nutzbook.util.RedisInterceptor.*;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class RedisService {
	
	@Aop("redis")
	public void set(String key, String val) {
		jedis().set(key, val);
	}

	@Aop("redis")
	public String get(String key) {
		return jedis().get(key);
	}
}
