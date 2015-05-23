package net.wendal.nutzbook.bean.form;

import java.util.List;
import java.util.Map;

import net.wendal.nutzbook.bean.BasePojo;
import net.wendal.nutzbook.bean.User;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

/**
 * 动态表单映射类
 * @author wendal
 *
 */
@Table("t_dy_form")
public class DyForm extends BasePojo {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;
	
	@Name
	private String name;
	
	@Column("tpl")
	@ColDefine(width=10240)
	private String template;
	
	@Column("u_id")
	private long userId;
	
	@Column("pa")
	@ColDefine(width=10240)
	private String parse;
	@Many(target=DyFormField.class, field="formId")
	List<DyFormField> data;
	@One(target=User.class, field="userId")
	private User user;
	
	
	// 非数据库字段
	private Map<String, DyFormField> add_fields;
	private int fields;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getParse() {
		return parse;
	}
	public void setParse(String parse) {
		this.parse = parse;
	}
	public List<DyFormField> getData() {
		return data;
	}
	public void setData(List<DyFormField> data) {
		this.data = data;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Map<String, DyFormField> getAdd_fields() {
		return add_fields;
	}
	public void setAdd_fields(Map<String, DyFormField> add_fields) {
		this.add_fields = add_fields;
	}
	public int getFields() {
		return fields;
	}
	public void setFields(int fields) {
		this.fields = fields;
	}
	
	
	
}
