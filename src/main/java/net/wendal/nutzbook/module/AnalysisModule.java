package net.wendal.nutzbook.module;

import static net.wendal.nutzbook.util.RedisInterceptor.jedis;

import java.util.Date;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import net.wendal.nutzbook.bean.yvr.Topic;
import net.wendal.nutzbook.service.SysConfigureService;
import net.wendal.nutzbook.util.Toolkit;

@IocBean
@At("/analysis")
@Ok("json")
public class AnalysisModule extends BaseModule {
    
    @Inject
    SysConfigureService sysConfigureService;

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
	
	@At("/user/topic/?")
	public Object userTopic(int uid, 
			@Param(value="start", dfmt="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")Date start, 
			@Param(value="end", dfmt="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")Date end) {
		NutMap map = new NutMap();
		if (start == null)
			start = new Date(System.currentTimeMillis() - 365L*86400*1000);
		if (end == null)
			end = new Date();
		List<Topic> list = Daos.ext(dao, FieldFilter.create(Topic.class, "createTime")).query(Topic.class, Cnd.where("userId", "=", uid).and("createTime", "between", new Object[]{start, end}));
		for (Topic topic : list) {
			map.put(""+(topic.getCreateTime().getTime() / 1000), 1);
		}
		return map;
	}
	
	@At(value="/sysconf/reload", top=true)
	public void sysconfReload() {
	    sysConfigureService.doReload();
	}
}
