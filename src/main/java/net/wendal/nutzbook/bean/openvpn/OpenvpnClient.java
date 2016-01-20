package net.wendal.nutzbook.bean.openvpn;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import net.wendal.nutzbook.bean.BasePojo;

@Table("t_openvpn_client")
public class OpenvpnClient extends BasePojo {

	private static final long serialVersionUID = -5191001915133806177L;

	@Id
	protected int id;
	@Name
	protected String ip;
	@Column
	protected String macid;
	@Column("pf")
	protected String platform;
	@Column("_key")
	protected String key;
	@Column("f")
	protected String file;
	@Column("stat")
	protected int status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMacid() {
		return macid;
	}
	public void setMacid(String macid) {
		this.macid = macid;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	
}
