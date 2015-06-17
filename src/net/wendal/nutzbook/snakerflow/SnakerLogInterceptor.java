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
				StringBuffer buffer = new StringBuffer(100);
				buffer.append("创建任务[标识=").append(task.getId());
				buffer.append(",名称=").append(task.getDisplayName());
				buffer.append(",创建时间=").append(task.getCreateTime());
				buffer.append(",参与者={");
				if(task.getActorIds() != null) {
					buffer.append(Strings.join(";", task.getActorIds()));
				}
				buffer.append("}]");
				log.info(buffer.toString());
			}
		}
	}

}
