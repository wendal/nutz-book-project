package net.wendal.nutzbook;

import java.util.HashMap;
import java.util.Map;

import net.wendal.nutzbook.bean.UserProfile;

import org.beetl.core.BeetlKit;
import org.junit.Assert;
import org.junit.Test;
import org.nutz.lang.util.NutMap;

public class SimpleTest {

	@Test
	public void test_string_array() {
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("list", new String[]{"hi"});
	    String re = BeetlKit.render("${list.~size}", params);
	    System.out.println(re);
	    System.out.println((Object[])params.get("list"));
	}
	
	@Test
	public void test_userprofile_userId() {
		UserProfile profile = new UserProfile();
		profile.setUserId(1);
		
		String uid = BeetlKit.render("${profile.userId}", new NutMap().setv("profile", profile));
		Assert.assertEquals("1", uid);
	}
}
