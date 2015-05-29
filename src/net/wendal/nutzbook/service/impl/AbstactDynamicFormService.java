package net.wendal.nutzbook.service.impl;

import org.apache.commons.lang.StringUtils;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.repo.Base64;
import org.nutz.service.IdNameEntityService;

import net.wendal.nutzbook.bean.form.DyForm;
import net.wendal.nutzbook.bean.form.DyFormField;
import net.wendal.nutzbook.service.DynamicFormService;

public abstract class AbstactDynamicFormService extends IdNameEntityService<DyForm> implements DynamicFormService {

	private static final Log log = Logs.get();
	
	public String html(DyForm form) {
		if (form == null || form.getParse() == null) {
			return "";
		}
		String p = form.getParse();
		int index = 0;
		for (DyFormField field : form.getData()) {
			log.debugf("name=%s, p=%s", field.getName(), field.getContent());
			if (field == null || field.getLeipiplugins() == null)
				continue;
			switch (field.getLeipiplugins()) {
			case "qrcode":
				String home_base = Mvcs.getServletContext().getContextPath();
				String url = home_base + "/qrcode/get?data="+ Base64.encodeToString(field.getValue().getBytes(), false);
				p = StringUtils.replace(p, "{"+field.getName()+"}", "<img src='"+url+"' style='"+field.getStyle() +"'>");
				break;
			case "checkboxs":
				p = StringUtils.replace(p, "{checkboxs_" + index +"}", field.getContent());
				index++;
				break;
			default:
				p = StringUtils.replace(p, "{"+field.getName()+"}", field.getContent());
				break;
			}
		}
		return p;
	}
}
