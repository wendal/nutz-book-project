package net.wendal.nutzbook.bean;

import net.wendal.nutzbook.bean.form.DyForm;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

/**
 * snaker不允许直接扩展器Process类,然后又需要存储流程定义的额外信息,所以加这个类
 * @author wendal
 *
 */
@Table("t_process_ext")
public class ProcessExt {

	@Name
	private String processId;
	
	/**
	 * 动态表单的id
	 */
	@Column("formid")
	private long formId;
	
	/**
	 * 修改者的Id
	 */
	@Column("u_id")
	private long userId;
	
	@Column("svg")
	private byte[] svg;
	
	@One(target=DyForm.class, field="formId")
	private DyForm form;
	
	@One(target=User.class, field="userId")
	private User user;

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public DyForm getForm() {
		return form;
	}

	public void setForm(DyForm form) {
		this.form = form;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public byte[] getSvg() {
		return svg;
	}

	public void setSvg(byte[] svg) {
		this.svg = svg;
	}
	
	
}
