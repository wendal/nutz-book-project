package net.wendal.nutzbook.bean.form;

import java.io.Serializable;

public class DyFormFieldOption implements Serializable {
	private static final long serialVersionUID = 6338016404696005641L;
	private String value;
	private String type;
	private String checked;
	private String name;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
