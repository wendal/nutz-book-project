package net.wendal.nutzbook.snakerflow;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.snaker.engine.SnakerInterceptor;
import org.snaker.engine.core.Execution;
import org.snaker.engine.entity.Task;

public class SnakerLogInterceptor implements SnakerInterceptor {

	private static final Log log = Logs.get();
	/**
	 * 拦截产生的任务对象，打印日志
	 */
	public void intercept(Execution execution) {
		if(log.isInfoEnabled()) {
			for(Task task : execution.getTasks()) {
				StringBuilder sb = new StringBuilder(100);
				sb.append("创建任务[标识=").append(task.getId());
				sb.append(",名称=").append(task.getDisplayName());
				sb.append(",创建时间=").append(task.getCreateTime());
				sb.append(",参与者={");
				if(task.getActorIds() != null) {
					sb.append(Strings.join(";", task.getActorIds()));
				}
				sb.append("}]");
				log.info(sb);
			}
		}
	}

}
