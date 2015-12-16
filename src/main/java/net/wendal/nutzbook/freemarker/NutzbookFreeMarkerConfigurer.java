package net.wendal.nutzbook.freemarker;

import javax.servlet.ServletContext;

import org.nutz.plugins.view.freemarker.FreeMarkerConfigurer;
import org.nutz.plugins.view.freemarker.FreemarkerDirectiveFactory;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelException;
import net.wendal.nutzbook.util.Toolkit;

import java.util.Map;

public class NutzbookFreeMarkerConfigurer extends FreeMarkerConfigurer {

	public NutzbookFreeMarkerConfigurer() {
		super();
	}

	public NutzbookFreeMarkerConfigurer(Configuration configuration, ServletContext sc, String prefix, String suffix, FreemarkerDirectiveFactory freemarkerDirectiveFactory) {
		super(configuration, sc, prefix, suffix, freemarkerDirectiveFactory);
		try {
			configuration.setAllSharedVariables(new SimpleHash(Toolkit.getTemplateShareVars()));
		} catch (TemplateModelException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTags(Map<String, Object> map) {
		super.setTags(map);
	}
}
