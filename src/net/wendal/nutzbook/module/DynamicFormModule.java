package net.wendal.nutzbook.module;

import net.wendal.nutzbook.bean.form.DyForm;
import net.wendal.nutzbook.service.DynamicFormService;

import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;

@IocBean
@At("/admin/form")
@Ok("json")
public class DynamicFormModule extends BaseModule {

	private static final Log log = Logs.get();
	
	@Inject(optional=true) 
	protected DynamicFormService dynamicFormService;
	
	@At("/")
	@Ok("jsp:jsp.form.index")
	public void index(){}
	
	//---------------------- 维护方法
	public Object list(@Param("..")Pager pager, @Param("query")String query) {
		return ajaxOk(dynamicFormService.list(query, pager));
	}
	
	public void delete(long id) {
		dynamicFormService.delete(id);
	}
	
	public void update(DyForm form) {
		dynamicFormService.update(form);
	}
	
	
	@At("/design/")
	@Ok("jsp:/admin/form/design/index.jsp")
	public void design() {
	}
	
	@At("/design/?")
	@Ok("jsp:/admin/form/design/index.jsp")
	public Object designPage(long formId) {
		DyForm form = dao.fetch(DyForm.class, formId);
		if (form == null) {
			return new HttpStatusView(404);
		}
		return form;
	}
	
	@At
	public Object save(@Param("type")String type, @Param("formid")Long formid, 
			@Param("form_name")String form_name, @Param("parse_form")String parse_form,
			@Attr("me")long userId){
		
		DyForm form = Json.fromJson(DyForm.class, parse_form);
		log.debug(Json.toJson(form));
		if (Strings.isBlank(form.getName())) {
			form.setName(form_name);
		}
		form.setUserId(userId);
		if (formid != null) {
			form.setId(formid);
			dynamicFormService.update(form);
		} else {
			dynamicFormService.add(form);
		}
		return ajaxOk(dynamicFormService.html(form));
	}
}
