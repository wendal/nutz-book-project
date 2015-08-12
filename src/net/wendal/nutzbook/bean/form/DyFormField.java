package net.wendal.nutzbook.bean.form;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_dy_form_field")
public class DyFormField implements Serializable {
	@Id
	private long id;
	@Column
	private long formId;
	@Column
	private String style;
	@Column
	private String title;
	@Column
	private String value;
	@Column
	private String name;
	@Column
	private String orgheight;
	@Column
	private String orgwidth;
	@Column
	private String orgalign;
	@Column
	private String orgfontsize;
	@Column
	private String orghide;
	@Column
	private String leipiplugins;
	@Column
	private String orgtype;
	@Column
	@ColDefine(width=4096)
	private String content;
	
	/*非数据库字段*/
	private List<DyFormFieldOption> options;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrgheight() {
		return orgheight;
	}

	public void setOrgheight(String orgheight) {
		this.orgheight = orgheight;
	}

	public String getOrgwidth() {
		return orgwidth;
	}

	public void setOrgwidth(String orgwidth) {
		this.orgwidth = orgwidth;
	}

	public String getOrgalign() {
		return orgalign;
	}

	public void setOrgalign(String orgalign) {
		this.orgalign = orgalign;
	}

	public String getOrgfontsize() {
		return orgfontsize;
	}

	public void setOrgfontsize(String orgfontsize) {
		this.orgfontsize = orgfontsize;
	}

	public String getOrghide() {
		return orghide;
	}

	public void setOrghide(String orghide) {
		this.orghide = orghide;
	}

	public String getLeipiplugins() {
		return leipiplugins;
	}

	public void setLeipiplugins(String leipiplugins) {
		this.leipiplugins = leipiplugins;
	}

	public String getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<DyFormFieldOption> getOptions() {
		return options;
	}

	public void setOptions(List<DyFormFieldOption> options) {
		this.options = options;
	}
}
