package net.wendal.nutzbook.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.nutz.lang.Times;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 格式化date
 */
public class TimeFormatDirective implements TemplateDirectiveModel {
	public static final String PARAM_TIME = "time";
	public static final String PARAM_FORMAT = "format";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		String time = DirectiveUtils.getString(PARAM_TIME, params);
		String format = DirectiveUtils.getString(PARAM_FORMAT, params);
		Writer out = env.getOut();
		String formatTime = Times.format(format, Times.D(NumberUtils.toLong(time)));
		out.append(formatTime);
	}
	public static void main(String[] args) {
		String formatTime = Times.format("yyyy-MM", Times.now());//Times.D("1389076676271"));
		System.out.print(formatTime);
		System.out.println(Times.D(1389076676271L));
	}
}
