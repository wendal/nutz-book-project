package net.wendal.nutzbook.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wendal.nutzbook.bean.form.DyForm;
import net.wendal.nutzbook.bean.form.DyFormData;

import org.nutz.dao.FieldFilter;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;

public class OrmDynamicFormService extends AbstactDynamicFormService {
	
	public DyForm fetch(long id) {
		DyForm form = dao().fetch(DyForm.class, id);
		if (form == null)
			return null;
		return dao().fetchLinks(form, "data");
	}

	public DyForm add(DyForm form) {
		return dao().insert(form);
	}
	
	public int count(String query) {
		return dao().count(DyForm.class);
	}

	public List<DyForm> list(String query, Pager pager) {
		return dao().query(DyForm.class, null, pager);
	}

	public void update(DyForm form) {
		form.setUpdateTime(new Date());
		Daos.ext(dao(), FieldFilter.create(DyForm.class, null, "name", true)).updateWith(form, null);
	}

	public DyForm clone(long id, String name) {
		DyForm form = fetch(id);
		if (form != null) {
			form = dao().fetchLinks(form, null);
			form.setName(name);
			return dao().insertWith(form, "data");
		}
		return null;
	}

	public long insertFormData(DyForm form, Map<String, Object> data) {
		if (data == null)
			data = new HashMap<String, Object>();
		DyFormData dfd = new DyFormData();
		dfd.setFormId(form.getId());
		dfd.setData(Json.toJson(data, JsonFormat.full().setIndent(0)).getBytes());
		dao().insert(dfd);
		return dfd.getId();
	}

	public Map<String, Object> fetchFormData(long id) {
		DyFormData dfd =  dao().fetch(DyFormData.class, id);
		if (dfd == null || dfd.getData() == null)
			return new HashMap<String, Object>();
		return Json.fromJson(NutMap.class, new InputStreamReader(new ByteArrayInputStream(dfd.getData())));
	}

}
