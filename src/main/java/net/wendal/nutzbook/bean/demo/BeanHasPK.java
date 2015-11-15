package net.wendal.nutzbook.bean.demo;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

@PK({"pkA", "pkB"})
@Table("t_demo_test_pks")
public class BeanHasPK {

	@Column("pka")
	private String pkA;
	@Column("pkb")
	private String pkB;
	public String getPkA() {
		return pkA;
	}
	public void setPkA(String pkA) {
		this.pkA = pkA;
	}
	public String getPkB() {
		return pkB;
	}
	public void setPkB(String pkB) {
		this.pkB = pkB;
	}
}
