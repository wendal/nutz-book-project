package net.wendal.nutzbook.service.syslog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.wendal.nutzbook.bean.SysLog;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create="init", depose="close")
public class SysLogService implements Runnable {
	
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
	
	public void sync(SysLog syslog) {
		try {
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
	}
	
	public void close() throws InterruptedException {
		queue = null; // 触发关闭
		if (es != null && !es.isShutdown()) {
			es.shutdown();
			es.awaitTermination(5, TimeUnit.SECONDS);
		}
	}
}
