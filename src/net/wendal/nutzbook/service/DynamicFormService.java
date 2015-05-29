package net.wendal.nutzbook.service;

import java.util.List;
import java.util.Map;

import org.nutz.dao.pager.Pager;

import net.wendal.nutzbook.bean.form.DyForm;

public interface DynamicFormService {
	
	// 动态表单定义相关
	DyForm fetch(long id);
	
	DyForm fetch(String name);

	DyForm add(DyForm form);
	
	String html(DyForm form);
	
	List<DyForm> list(String query, Pager pager);
	
	int delete(long id);
	
	void update(DyForm form);
	
	DyForm clone(long id, String name);
	
	// 表单数据相关
	
	long insertFormData(DyForm form, Map<String, Object> data);
	
	Map<String, Object> fetchFormData(long id);

	int count(String query);
}
