package net.wendal.nutzbook;
import static org.mockito.Mockito.when;

import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;

import org.beetl.core.BeetlKit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.NutType;
import org.nutz.mapl.Mapl;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.ParamExtractor;
import org.nutz.mvc.adaptor.Params;
import org.nutz.mvc.adaptor.injector.ObjectNaviNode;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.admin.DataTableColumn;
import net.wendal.nutzbook.bean.demo.APIResult;
import net.wendal.nutzbook.bean.qqbot.QQBotMessage;
import net.wendal.nutzbook.bean.qqbot.QQBotRole;
import net.wendal.nutzbook.util.Markdowns;

public class SimpleTest extends TestBase {

	@Test
	public void test_string_array() {
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("list", new String[]{"hi"});
	    String re = BeetlKit.render("${list.~size}", params);
	    System.out.println(re);
	    System.out.println(params.get("list"));
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
	
	@Test
	public void test_json_output() {
		APIResult re = new APIResult();
		re.setCode(200);
		re.setMessage("null");
		re.setResult(new NutMap());
		System.out.println(Json.toJson(re));
	}
	
//	@Test
//	public void test_jdk8_param_name() throws IOException{
//		Map<String, List<String>> names = MethodParamNamesScaner.getParamNames(YvrModule.class);
//		for (Entry<String, List<String>> en : names.entrySet()) {
//			System.out.println(en.getValue());
//		}
//	}
	
	@Test
	public void test_oracle_timestamp() {
		System.out.println(new Timestamp(System.currentTimeMillis()));
	}
	
	/**
	 * 更新或插入对象
	 * @param dao Dao实例
	 * @param list 需要操作的列表
	 * @param field 作为判定依据的属性名称
	 */
	public static <T> void insertOrUpdate(Dao dao, List<T> list, String field) {
		Entity<? extends Object> en = dao.getEntity(list.get(0).getClass());
		MappingField mf = en.getField(field);
		for (T t : list) {
			Object val = mf.getValue(t);
			// 如果对应的属性不为null,且不为0,到数据库检查一下是否存在
			if (val != null && !(val instanceof Number && ((Number)val).intValue() == 0)) {
				// count一下就知道是否存在了
				if (dao.count(t.getClass(), Cnd.where(field, "=", val)) != 0) {
					dao.update(t); // 存在,更新之
					continue;
				}
			}
			dao.insert(t);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void test_mirror_map_get() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("abc", "123");
		Mirror mirror = Mirror.me(map.getClass());
		mirror.getValue(map, "abc");
		
	}
	
	@Test
	public void test_sql_cnd() {
		Sql sql = Sqls.create("select * from WebSite @cnd and Status != -1");
		sql.setParam("cnd", Cnd.where("id", ">", 0));
		System.out.println(sql);
	}

	@Test
	public void test_eval_js() throws Exception {
		QQBotMessage message = new QQBotMessage();
		QQBotRole role = new QQBotRole();
		role.executeParams = "";
		role.executeBody = "return 1;";
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine engine = sem.getEngineByExtension("js");
		SimpleScriptContext ctxt = new SimpleScriptContext();
		ctxt.setAttribute("ioc", Mvcs.getIoc(), ScriptContext.ENGINE_SCOPE);
		ctxt.setAttribute("message", message, ScriptContext.ENGINE_SCOPE);
		ctxt.setAttribute("role", role, ScriptContext.ENGINE_SCOPE);
		Object result = engine.eval("function _qqbot(){"+ role.executeBody + "};_qqbot();", ctxt);
		System.out.println(result);
	}

	@Test
	public void test_complex_prefix() throws Exception {
		String params = "draw=1&columns%5B0%5D%5Bdata%5D=userId&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=loginname&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=nickname&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=asc&start=0&length=10&search%5Bvalue%5D=&search%5Bregex%5D=false";
		NutMap map = new NutMap();
		for (String kv : params.split("&")) {
			System.out.println(kv);
			String[] tmp = kv.split("=");
			String key = URLDecoder.decode(tmp[0], "UTF-8");
			String value = URLDecoder.decode(tmp.length > 1 ? tmp[1] : "", "UTF-8");
			map.put(key, value);
		}
		System.out.println(map);
		String prefix = "columns";
		Object refer = map;
		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		when(req.getParameterMap()).thenReturn(new HashMap<String, String[]>());

		ObjectNaviNode no = new ObjectNaviNode();
		String pre = "";
		if ("".equals(prefix))
			pre = "node.";
		ParamExtractor pe = Params.makeParamExtractor(req, refer);
		for (Object name : pe.keys()) {
			String na = (String) name;
			if (na.startsWith(prefix)) {
				no.put(pre + na, pe.extractor(na));
			}
		}
		Object model = no.get();
		Object re = Mapl.maplistToObj(model, NutType.list(DataTableColumn.class));
		System.out.println(re);
	}
	
	@Test
	public void test_mirror_get_fields() {
		System.out.println(Arrays.toString(Mirror.me(NutDao.class).getFields()));
	}
	
//	@Test
//	public void test_1000_sql() {
//		Dao dao = ioc.get(Dao.class);
//		
//		Stopwatch sw = Stopwatch.begin();
//		//dao.query("car_config", null);
//		List<CarConfig> list = dao.query(CarConfig.class, null, dao.createPager(1, 3000));
//		sw.stop();
//		System.out.println(sw);
//		System.out.println(list.size());
//		System.out.println(list.get(1).CAR_BRAND);
//	}
}
