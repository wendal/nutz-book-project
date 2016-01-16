package net.wendal.nutzbook.module;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import net.wendal.nutzbook.util.Toolkit;

@IocBean
@At("/analysis")
@Ok("json")
public class AnalysisModule extends BaseModule {

	@At({"/", "/index"})
	@Ok("beetl:yvr/analysis/index.btl")
	@Aop("redis")
	public void main() {
	}
	
	@At("/user/history/day")
	public Object userOnlineDayHistory(@Param("day")String day) {
		if (Strings.isBlank(day))
			day = Toolkit.today_yyyyMMdd();
		NutMap re = new NutMap();
		Long dayCount = jedis().bitcount(RKEY_ONLINE_DAY + day);
		re.put("day", dayCount);
		Long[] hours = new Long[24];
		for (int i = 0; i < 24; i++) {
			Long hourCount = jedis().bitcount(RKEY_ONLINE_HOUR + String.format("%s%02d", day, i+1));
			hours[i] = hourCount;
		}
		re.put("hours", hours);
		return re;
	}
	
	@At("/user/history/between")
	public Object userOnlineDayHistoryBetween(@Param("start")String start, @Param("end")String end) {
		return null;
	}
}
