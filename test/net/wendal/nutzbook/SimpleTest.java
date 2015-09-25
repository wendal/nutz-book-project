package net.wendal.nutzbook;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.util.Markdowns;

import org.beetl.core.BeetlKit;
import org.junit.Assert;
import org.junit.Test;
import org.nutz.lang.util.NutMap;

public class SimpleTest extends Assert {

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
		String re = Markdowns.toHtml(md, null);
		System.out.println(re);
	}
	
//	@Test
//	public void test_beetl_objectutl_for_enum () {
//		MethodInvoker invoker = ObjectUtil.getInvokder(TopicType.class, "name");
//		assertNotNull(invoker);
//		TopicType.ask.name();
//	}
	
//	@Test
//	public void test_github_code() throws IOException {
//		InputStream ins = getClass().getClassLoader().getResourceAsStream("net/wendal/nutzbook/md/github_code.md");
//		Reader r = new InputStreamReader(ins);
//		String re = Markdowns.toHtml(Streams.read(r).toString());
//		System.out.println(re);
//	}
//	
//	@Test
//	public void test_url() {
//		String url=new String(Base64.decode("aHR0cDovL3Bob3RvLnNjb2wuY29tLmNuL3Nqc2MvMjAxNTA5LzU0MDE2NzI3Lmh0bWwg"));
//        System.out.println("Decoder url: ["+ url + "]");
//	}
}
