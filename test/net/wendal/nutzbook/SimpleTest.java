package net.wendal.nutzbook;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.util.Markdowns;

import org.beetl.core.BeetlKit;
import org.junit.Assert;
import org.junit.Test;
import org.nutz.lang.Lang;
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
		
		NutMap obj = new NutMap();
		obj.setv("list", Collections.EMPTY_LIST);
		obj.setv("profile", profile);
		String uid = BeetlKit.render("${obj.list.~size}${obj.profile.userId}", new NutMap().setv("obj", obj));
		Assert.assertEquals("01", uid);
	}
	
	@Test
	public void markdown_code() {
		String md = "```\npublic class MainModule{}<img ><pre></pre>\n```";
		String re = Markdowns.toHtml(md);
		System.out.println(re);
	}
}
