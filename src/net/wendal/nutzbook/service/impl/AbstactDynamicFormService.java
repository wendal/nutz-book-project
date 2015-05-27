package net.wendal.nutzbook.service.impl;

import org.nutz.service.IdNameEntityService;

import net.wendal.nutzbook.bean.form.DyForm;
import net.wendal.nutzbook.service.DynamicFormService;

public abstract class AbstactDynamicFormService extends IdNameEntityService<DyForm> implements DynamicFormService {

	public String html(DyForm form) {
		return null;
	}
}
