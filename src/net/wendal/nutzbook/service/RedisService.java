package net.wendal.nutzbook.service;

import static net.wendal.nutzbook.util.RedisInterceptor.*;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 演示用途的Redis服务
 * @author wendal
 *
 */
@IocBean
public class RedisService {
	
	@Aop("redis") // 通过aop拦截获取jedis实例
	public void set(String key, String val) {
		jedis().set(key, val);
	}

	@Aop("redis")
	public String get(String key) {
		return jedis().get(key);
	}
}
