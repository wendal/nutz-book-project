package net.wendal.nutzbook.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableMeta;

@Table("t_logs")
@TableMeta("{'mysql-engine':'CSV'}")
public class SystemLog {

	@Column(hump=true)
	@ColDefine(width=10, notNull=true)
	protected String logLevel;
	@Column(hump=true)
	@ColDefine(width=2028, notNull=true)
	protected String className;
	@Column(hump=true)
	@ColDefine(width=1024, notNull=true)
	protected String methodName;
	@Column(hump=true)
	@ColDefine(notNull=true)
	protected String lineNumber;
	@Column(hump=true)
	@ColDefine(width=2028, notNull=true)
	protected String mid;
	@Column(hump=true)
	@ColDefine(width=10000, notNull=true)
	protected String content;
	@Column(hump=true)
	@ColDefine(width=64, notNull=true)
	protected String insertTime;
}
