package net.wendal.nutzbook.bean;

import java.util.Date;

import net.wendal.nutzbook.util.Toolkit;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_syslog_${ym}")
public class SysLog {

	@Id
	private long id;
	
	@Column("t")// aop.before aop.after aop.error
	private String t;
	
	@Column("tg")
	private String tag; 
	
	@Column("src")
	@ColDefine(width=1024)
	private String source;
	
	@Column("u_id")
	private int uid;
	
	@Column("ip")
	private String ip;
	
	@Column
	@ColDefine(width=8192)
	private String msg;

	@Column("ct")
	protected Date createTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public static SysLog c(String t, String tag, String source, int uid, String msg) {
		SysLog sysLog = new SysLog();
		sysLog.setCreateTime(new Date());
		if (t == null || tag == null || msg == null) {
			throw new RuntimeException("t/tag/msg can't null");
		}
		if (source == null) {
			StackTraceElement[] tmp = Thread.currentThread().getStackTrace();
			if (tmp.length > 2) {
				source = tmp[2].getClassName() + "#" + tmp[2].getMethodName();
			} else {
				source = "main";
			}
		}
		sysLog.t = t;
		sysLog.tag = tag;
		sysLog.source = source;
		sysLog.uid = uid;
		sysLog.msg = msg;
		sysLog.ip = Toolkit.ip();
		
		return sysLog;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
