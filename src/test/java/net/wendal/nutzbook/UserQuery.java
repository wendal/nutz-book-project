package net.wendal.nutzbook;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Test;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.lang.Mirror;

public class UserQuery {

	@Column("=")
	private String id;

	@Column("like")
	private String name;

	@Column("asc")
	private String updateTime;

	@Column("desc")
	private String createTime;

	@Test
	public void test() {
		Mirror<?> mirror = Mirror.me(this.getClass());
		Field[] fields = mirror.getFields(Column.class); // fileds为空数组
		System.out.println(Arrays.toString(fields));
	}
}