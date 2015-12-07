package net.wendal.nutzbook.service.syslog;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.zbus.annotation.ZBusConsumer;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;

import net.wendal.nutzbook.bean.SysLog;

@ZBusConsumer(mq="syslog")
@IocBean(create="init", depose="close")
public class SysLogService implements Runnable, MessageHandler {
	
	private static final Log log = Logs.get();
	
	ExecutorService es;
	
	LinkedBlockingQueue<SysLog> queue;

	@Inject
	protected Dao dao;
	
	public void async(SysLog syslog) {
		LinkedBlockingQueue<SysLog> queue = this.queue;
		if (queue != null)
			try {
				boolean re = queue.offer(syslog, 50, TimeUnit.MILLISECONDS);
				if (!re) {
					log.info("syslog queue is full, drop it ...");
				}
			} catch (InterruptedException e) {
			}
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
	
	public void run() {
		while (true) {
			LinkedBlockingQueue<SysLog> queue = this.queue;
			if (queue == null)
				break;
			try {
				SysLog sysLog = queue.poll(1, TimeUnit.SECONDS);
				if (sysLog != null) {
					sync(sysLog);
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public void init() {
		queue = new LinkedBlockingQueue<SysLog>();
		int c = Runtime.getRuntime().availableProcessors();
		es = Executors.newFixedThreadPool(c);
		for (int i = 0; i < c; i++) {
			es.submit(this);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -2);
		for (int i = 0; i < 38; i++) {
			Dao dao = Daos.ext(this.dao, String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1));
			dao.create(SysLog.class, false);
			cal.add(Calendar.MONTH, 1);
		}
	}
	
	public void close() throws InterruptedException {
		queue = null; // 触发关闭
		if (es != null && !es.isShutdown()) {
			es.shutdown();
			es.awaitTermination(5, TimeUnit.SECONDS);
		}
	}
	
	public void handle(Message msg, Session sess) throws IOException {
		SysLog sysLog = Json.fromJson(SysLog.class, msg.getBodyString());
		this.sync(sysLog);
	}
}
