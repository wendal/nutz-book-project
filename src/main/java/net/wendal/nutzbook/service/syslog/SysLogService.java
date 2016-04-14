package net.wendal.nutzbook.service.syslog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.wendal.nutzbook.bean.SysLog;

@IocBean(create="init")
public class SysLogService {
	
	private static final Log log = Logs.get();
	
	@Inject
	protected Dao dao;
	
	@Async
	public void async(SysLog syslog) {
		this.sync(syslog);
	}
	
	public static final SimpleDateFormat ym = new SimpleDateFormat("yyyyMM");
	
	public void sync(SysLog syslog) {
		try {
			// TODO syslog需要改成更高效的方式
			Dao dao = Daos.ext(this.dao, ((DateFormat)ym.clone()).format(syslog.getCreateTime()));
			dao.fastInsert(syslog);
		} catch (Throwable e) {
			log.info("insert syslog sync fail", e);
		}
	}
	
	public void init() {
		checkTable();
	}
	
	public void checkTable() {
		Calendar cal = Calendar.getInstance();
		Dao dao = Daos.ext(this.dao, String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1));
		dao.create(SysLog.class, false);
		cal.add(Calendar.MONTH, 1);
		dao = Daos.ext(this.dao, String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1));
		dao.create(SysLog.class, false);
	}
}
