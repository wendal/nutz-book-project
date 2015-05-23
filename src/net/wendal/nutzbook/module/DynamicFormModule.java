package net.wendal.nutzbook.module;

import net.wendal.nutzbook.bean.form.DyForm;
import net.wendal.nutzbook.bean.form.DyFormField;

import org.nutz.dao.Sqls;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;

@IocBean
@At("/admin/form")
@Ok("json")
public class DynamicFormModule extends BaseModule {

	private static final Log log = Logs.get();
	
	private String tableNameSeg = "t_dy_form_${id}";
	
	@At("/")
	@Ok("jsp:jsp.form.index")
	public void index(){}
	
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
			@Param("form_name")String form_name, @Param("parse_form")String parse_form){
		log.debug("type=" + type);
		log.debug("formid=" + formid);
		log.debug("parse_form=" + parse_form);
		DyForm form = Json.fromJson(DyForm.class, parse_form);
		log.debug(Json.toJson(form));
		if (Strings.isBlank(form.getName())) {
			form.setName(form_name);
		}
		parse_table(formid, form);
		return ajaxOk(form.getTemplate().replaceAll("\\{\\|\\-", "").replaceAll("\\-\\|\\}", ""));
	}
	
	public void parse_table(Long formid, DyForm form){
		if (formid == null) {
			dao.insertWith(form, null);
			formid = form.getId();
		}
		String tableName = new CharSegment(tableNameSeg).render(Lang.context().set("id", formid)).toString();
		if (dao.exists(tableName)) {
			// 逐一检查字段,看看要添加什么
		} else {
			StringBuilder fields = new StringBuilder();
			for (DyFormField field : form.getAdd_fields().values()) {
				fields.append(field.getName()).append(" ").append(fieldColunmType(field)).append(",");
			}
			// 没有? 那就新建咯
			String sql = "CREATE TABLE `" + tableName + "` ("
		              + "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
		              + "`u_id` int(10) unsigned NOT NULL DEFAULT '0',"
		              + "`foreign_id` int(10) unsigned NOT NULL DEFAULT '0',"
		              + fields
		              + "`is_del` tinyint(1) unsigned NOT NULL DEFAULT '0',"
		              + "`updatetime` int(10) unsigned NOT NULL DEFAULT '0',"
		              + "`dateline` int(10) unsigned NOT NULL DEFAULT '0',"
		              + "PRIMARY KEY (`id`),"
		              + "KEY `uid` (`u_id`),"
		              + "KEY `foreign_id` (`foreign_id`)"
		            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			dao.execute(Sqls.create(sql));
		}
	}
	
	protected String fieldColunmType(DyFormField field) {
		// TODO 根据不同数据库做匹配
		switch (field.getLeipiplugins()) {
		case "textarea":
		case "listctrl":
			return "text NOT NULL";
		case "checkboxs":
			return "tinyint(1) UNSIGNED NOT NULL DEFAULT 0";
		default:
			return "varchar(256) NOT NULL DEFAULT ''";
		}
	}
}
