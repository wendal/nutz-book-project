package net.wendal.nutzbook;

import java.util.HashMap;
import java.util.Map;

import org.beetl.core.BeetlKit;
import org.junit.Test;

public class SimpleTest {

	@Test
	public void test_string_array() {
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("list", new String[]{"hi"});
	    String re = BeetlKit.render("${list.~size}", params);
	    System.out.println(re);
	    System.out.println((Object[])params.get("list"));
	}
}
