package net.wendal.nutzbook;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.beetl.core.BeetlKit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.aop.matcher.RegexMethodMatcher;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.el.El;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.NutType;
import org.nutz.log.Logs;
import org.nutz.mapl.Mapl;
import org.nutz.mvc.adaptor.ParamExtractor;
import org.nutz.mvc.adaptor.Params;
import org.nutz.mvc.adaptor.injector.ObjectNaviNode;

import net.sf.ehcache.CacheManager;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.admin.DataTableColumn;
import net.wendal.nutzbook.service.EmailServiceImpl;
import net.wendal.nutzbook.service.UserService;
import net.wendal.nutzbook.util.Markdowns;

@RunWith(NutBookIocTestRunner.class)
@IocBean
public class SimpleTest extends Assert {
    
    //private static final Log log = Logs.get();
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected Dao dao;

	@Test
	public void test_string_array() {
        System.out.println(dao.execute(Sqls.fetchRecord("select now()")).getObject(Record.class));
        System.out.println(dao.execute(Sqls.fetchRecord("select DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s')")).getObject(Record.class));
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
	
//	@Test
//	public void test_json_output() {
//		APIResult re = new APIResult();
//		re.setCode(200);
//		re.setMessage("null");
//		re.setResult(new NutMap());
//		System.out.println(Json.toJson(re));
//	}
//	
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

//	@Test
//	public void test_eval_js() throws Exception {
//		QQBotMessage message = new QQBotMessage();
//		QQBotRole role = new QQBotRole();
//		role.executeParams = "";
//		role.executeBody = "return 1;";
//		ScriptEngineManager sem = new ScriptEngineManager();
//		ScriptEngine engine = sem.getEngineByExtension("js");
//		SimpleScriptContext ctxt = new SimpleScriptContext();
//		ctxt.setAttribute("ioc", Mvcs.getIoc(), ScriptContext.ENGINE_SCOPE);
//		ctxt.setAttribute("message", message, ScriptContext.ENGINE_SCOPE);
//		ctxt.setAttribute("role", role, ScriptContext.ENGINE_SCOPE);
//		Object result = engine.eval("function _qqbot(){"+ role.executeBody + "};_qqbot();", ctxt);
//		System.out.println(result);
//	}

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
	
	@Test
	public void test_cast_short_array() {
		String strs = "4, 7, 0, 303, 350, 0, 303, 350, 0, 303, 350";
		System.out.println(Json.toJson(Json.fromJson(short[].class, "[" + strs + "]")));
	}
	
	@Test
	public void test_sql_in() {
		Sql sql = Sqls.create("select * from t_user where nm in (@ids)");
		sql.setParam("ids", new String[]{"wendal", "zozoh", "pangwu86"});
		System.out.println(sql);
		System.out.println(sql.toPreparedStatement());
	}
	
	@Test
	public void test_sql_in2() {
		Sql sql = Sqls.create("select * from t_user where nm in ($ids)");
		StringBuilder sb = new StringBuilder();
		String[] ids = new String[]{"wendal", "zozoh"};
		for (String id : ids) {
			sb.append("\"").append(Sqls.escapeFieldValue(id)).append("\"").append(",");
		}
		sql.setVar("ids", sb.substring(0, sb.length()-1));
		System.out.println(sql);
		System.out.println(sql.toPreparedStatement());
	}
	
	@Test
	public void aop_without_ioc() throws Exception {
		Class<EmailServiceImpl> type = EmailServiceImpl.class;
		ClassDefiner cd = DefaultClassDefiner.defaultOne();
		ClassAgent agent = new AsmClassAgent();
        agent.addInterceptor(new RegexMethodMatcher(".+"),
                                 new MethodInterceptor() {
									public void filter(InterceptorChain chain) throws Throwable {
										System.out.println("before");
										chain.doChain();
										System.out.println("after");
									}
								});
        Class<EmailServiceImpl> klass = agent.define(cd, type);
        EmailServiceImpl es = klass.newInstance();
        
        es.send(null, null, null);
	}
	
//	@Test
//	public void test_mysql_create_sql() {
//		NutDao dao = ioc.get(NutDao.class, "dao");
//		dao.setSqlManager(new FileSqlManager("net/wendal/nutzbook"));
//		dao.sqls().count();
//		dao.execute(dao.sqls().create("create.sql").setVar("year", 2016));
//		dao.execute(dao.sqls().create("create.sql2").setVar("year", 2016));
//		
//		dao.run(new ConnCallback() {
//			public void invoke(Connection conn) throws Exception {
//				// 写jdbc代码咯, 执行 select count(*) from xx where xxx = ?
//			}
//		});
//	}
	
//	public static void main(String[] args) throws Exception {
//		String source = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1bmlxdWVfbmFtZSI6IlpoYUJlaVBsYXRmb3JtU2VydmljZUFjY291bnQiLCJ6YjphdXRoOnNhIjp0cnVlLCJ1cm46b2F1dGg6ZXhwIjoiMTQ1ODY1ODI0NiIsInVybjpvYXV0aDppYXQiOiIxNDU4MDUzNDQ2IiwiaXNzIjoiaHR0cHM6Ly9hdXRoLnRjYy5zby8iLCJhdWQiOiJodHRwczovL3RjYy5zby8ifQ";
//		String target = "ZLhtk4rP4cSOL26JzhCop67xIPv4Adh5zEauieVk4W9HF3IsdpXCS1wAwKOhnMyVMowxJ9NpuPAtfoiqsWzl-AQ_8iAhR6QWAcelWkKk_QZvs-0DI8XcSeFjFgKSwBjvmcs1aXNrLDUtReVx09jukjaxtv02AH_SC8wkKTkE03mnmYEfEx7mDw7QQ8ZhYunKnFRejKXjwI62bcQU5tGCjIg97QiFlEUeNK1njfyuzGUM_AFGfWk3509TU5r97h_XdzSp2I3jfLYwHJGPW3GLZJZpq9FkmUqhwC3ER2mSxGQSfInRRdKzuZWx33wXJfXWd_BI49n7UqdiXyv_HuohzA" + "==";
//		//System.out.println(Base64.encodeToString(source.getBytes(), false));
//		byte[] buf = org.apache.commons.codec.binary.Base64.decodeBase64(target.getBytes());
//		System.out.println(Base64.encodeToString(buf, false));
//		//System.out.println(new String(org.apache.commons.codec.binary.Base64.encodeBase64(buf, true)));
//		System.out.println(new String(org.apache.commons.codec.binary.Base64.encodeBase64(buf, false)));
//		System.out.println(target);
//		System.out.println(Base64.encodeToString(buf, false).equals(target));
//		System.out.println(Base64.decode(target.replace('-', '+').replace('_', '=').getBytes()));
//		System.out.println(target.length() / 4.0);
//	}
	
//	@Test
//	public void sp_data() throws IOException {
//		byte[] buf = Files.readBytes("abc.data");
//		ByteArrayInputStream ins = new ByteArrayInputStream(buf);
//		byte[] b = new byte[1];
//		while (true) {
//			int len = ins.read(b);
//			if (len == -1)
//				break;
//			byte head = getBit(b[0], 8);
//			if (head == 0) {
//				System.out.print("短数据,");
//			} else {
//				System.out.print("长数据,");
//				ins.read(new byte[3]); // 3字节的时间数据
//			}
//			byte type = getBit(b[0], 7);
//			if (type == 0) {
//				System.out.print("串口数据,");
//				byte P = getBit(b[0], 6);
//				System.out.print("端口"+P+",");
//			} else {
//				System.out.print("控制数据,");
//			}
//			
//			ins.read(b);
//			System.out.println(Lang.fixedHexString(b));
//		}
//	}
	
	public static byte getBit(int ID, int position) {
	    return (byte) (ID >> (position - 1));
	}
	
	@Test
	public void test_left_join() throws Exception {
	    Dao dao = ioc.get(Dao.class);
        Entity<User> userEntity = dao.getEntity(User.class);
        Entity<UserProfile> profileEntity = dao.getEntity(UserProfile.class);
        
        Sql sql = Sqls.create("select u.* , p.* from t_user u left join t_user_profile p on u.id = p.u_id where u.name=@name");
        sql.params().set("name", "wendal");
        sql.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                if (!rs.next())
                    return null;
                Object user = userEntity.getMirror().born();
                Object profile = profileEntity.getMirror().born();
                ResultSetMetaData meta = rs.getMetaData();
                int count = meta.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    String name = meta.getColumnLabel(i);
                    String tableName = meta.getTableName(i);
                    if (tableName.equals(userEntity.getTableName())) {
                        MappingField mf = userEntity.getColumn(name);
                        if (mf != null)
                            mf.injectValue(user, rs, null);
                    } else if (tableName.equals(profileEntity.getTableName())) {
                        MappingField mf = profileEntity.getColumn(name);
                        if (mf != null)
                            mf.injectValue(profile, rs, null);
                    } 
                }
                return new Object[]{user, profile};
            }
        });
        Object[] re = dao.execute(sql).getObject(Object[].class);
        System.out.println(Json.toJson(re));
	}
	
	@Test
	public void test_new_weizhang() {
//	    Request req = Request.create("http://api.jisuapi.com/illegal/query", METHOD.GET);
//        req.getParams().put("appkey", "220bc7f5d1a4393d");
//        req.getParams().put("carorg", "beijing");
//        req.getParams().put("lsprefix", "京");
//        req.getParams().put("lsnum", "N582Y3");
//        req.getParams().put("lstype", "02");
//        //req.getParams().put("frameno", "WBA3X7103EDX98376");
//        req.getParams().put("engineno", "11168421N55B30A");
//        req.getParams().put("iscity", "1");
//        Response resp = Sender.create(req).send();
//        System.out.println(resp.getStatus());
//        System.out.println(resp.getContent());
	    
//	    Request req = Request.create("http://apis.haoservice.com/weizhang/EasyQuery", METHOD.GET);
//        req.getParams().put("key", "a7422a5d799d40b0894d91d228416e7c");
//        req.getParams().put("plateNumber", "京N582Y3");
//        req.getParams().put("engineNumber", "11168421N55B30A");
//        req.getParams().put("vehicleIdNumber", "WBA3X7103EDX98376");
//        req.getParams().put("cityName", "北京");
//        req.getParams().put("hpzl", "02");
//        
//      Response resp = Sender.create(req).send();
//      System.out.println(resp.getStatus());
//      System.out.println(resp.getContent());
	}

    @Test
    public void test_ioc_inject_by_setter() throws ObjectLoadException {
        AnnotationIocLoader loader = new AnnotationIocLoader(getClass().getPackage().getName());
        Logs.get().error(loader.load(null, "injectBySetter"));
        ioc.get(InjectBySetter.class);
    }
//    @Test
//    public void test_ioc_js() throws ObjectLoadException {
//        Ioc ioc = new NutIoc(new JsonLoader("fuck_dao.js"));
//        ioc.get(Dao.class);
//        ioc.depose();
//        
//        System.out.println(Long.parseLong("10"));
//    }
    
//    @Test
//    public void test_list_filter_jdk8() {
//        Dao dao = ioc.get(Dao.class);
//        List<UserProfile> list = dao.query(UserProfile.class, null);
//        long count = list.parallelStream().filter((user)->user.isEmailChecked()).count();
//    }
    
//    @Test
//    public void fir_im_version_check() {
//        Response resp = Http.get("http://download.fir.im/4qmu", 5*1000);
//        if (resp.isOK()) {
//            NutMap re = Json.fromJson(NutMap.class, resp.getContent());
//            re = re.getAs("app", NutMap.class);
//            String token = re.getString("token");
//            String id = re.getString("id");
//            re = re.getAs("releases", NutMap.class).getAs("master", NutMap.class);
//            String version = re.getString("version");
//            String release_id = re.getString("id");
//            //if (BuildConfig.VERSION_NAME.equals(version)) {
//                //popText("没有新版本");
//                //return;
//            //}
//            //popText("发现新版本: " + version);
//            String tmpl = "http://download.fir.im/apps/%s/install?download_token=%s&release_id=%s";
//            String url = String.format(tmpl, id, token, release_id);
//            //log.info("xplay.updater url="+url);
//            Request req = Request.create(url, Request.METHOD.GET);
//            req.getHeader().set("User-Agent", "curl/7.19.7 (x86_64-redhat-linux-gnu) libcurl/7.19.7 NSS/3.19.1 Basic ECC zlib/1.2.3 libidn/1.18 libssh2/1.4.2");
//            req.getHeader().set("Host", "download.fir.im");
//            resp = Sender.create(req).setTimeout(30*1000).send();
//            log.info("xplay.updater code="+resp.getStatus());
//            if (resp.isOK()) {
//                //popText("开始下载, 大小 " + resp.getHeader().getInt("Content-Length", 3*1024*1024) / 1024 + "kb");
//                Files.write(new File("xplay.apk"), resp.getStream());
////                if (Cmd.hasSu()) {
////                    String cmd = "pm install -r /sdcard/xplay.apk;\nam start com.danoo.androx.xplay/.MainActivity;";
////                    log.info("xplay.updater runAsRoot"+cmd);
////                    Cmd.runAsRoot(cmd);
////                } else {
////                    activity.runOnUiThread(new Runnable() {
////                        public void run() {
////                            Intent install = new Intent();
////                            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                            install.setAction(android.content.Intent.ACTION_VIEW);
////                            install.setDataAndType(Uri.fromFile(new File("/sdcard/xplay.apk")),"application/vnd.android.package-archive");
////                            activity.startActivity(install);
////                        }
////                    });
////                }
//            } else {
//
//            }
//        }
//    }
    
    @Test
    public void test_el() {
        El el = new El("'hi,'+name");
        Context ctx = Lang.context();
        ctx.set("name", "wendal");
        assertEquals("hi,wendal", el.eval(ctx));
    }
    

    @Test
    public void test_el2() throws Exception {
        El el = new El("sayhi(name)");
        Context ctx = Lang.context();
        ctx.set("name", "wendal");
        ctx.set("sayhi", getClass().getMethod("sayhi", String.class));
        assertEquals("hi,wendal", el.eval(ctx));
    }
    
    public static String sayhi(String name) {
        return "hi,"+name;
    }

    
    @Test
    public void test_fetch_count() {
        Dao dao = ioc.get(Dao.class);
        
        // 删表重建
        dao.drop("qiu_answer");
        dao.execute(Sqls.create("create table qiu_answer (uid int, type int)"));
        
        
        Sql sql = Sqls.create("select count(*) as adoptCount from qiu_answer where uid = @uid and type =1 ");
        sql.params().set("uid", 1);
        sql.setCallback(Sqls.callback.integer());
        dao.execute(sql);
        int count = sql.getNumber().intValue();
        assertEquals(0, count);
    }
    
    @Test
    public void test_ioc_factory_create() {
        Ioc ioc = new NutIoc(new JsonLoader(new StringReader("{abc:{factory:'net.wendal.nutzbook.SimpleTest#create'}}")));
        ioc.get(Object.class, "abc");
        ioc.depose();
    }
    
    public static Dao create() {
        return new NutDao();
    }
    
    @Test
    public void test_cnd_wrap() {
        Sql sql = Sqls.create("select * from user $condition");
        sql.setCondition(Cnd.wrap("1=1"));
        System.out.println(sql);
    }
    
    @Test
    public void test_pojo_sql() {
        Dao dao = ioc.get(Dao.class);
        dao.create(PojoSql.class, true);
        PojoSql pojo = new PojoSql();
        pojo.setName(R.UU32());
        pojo.setNickname(R.UU32());
        pojo.setCt(System.currentTimeMillis());
        dao.insert(pojo);
        
        assertEquals(pojo.getName(), pojo.getNickname());
        
        pojo.setNickname(null);
        pojo.setAge(20);
        pojo.setCt(0);
        Chain chain = Chain.from(pojo, FieldMatcher.make(null, null, true, true, true, true, true, true), dao);
        dao.update(PojoSql.class, chain, Cnd.where("id", "=", pojo.getId()));
    }
    
    @Test
    public void test_ioc_inject() throws Exception {
        Object obj = new Object();
        
        for (Field field : obj.getClass().getDeclaredFields()) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null)
                field.set(obj, ioc.get(field.getType(), field.getName()));
        }
    }
    
    @Test
    public void test_query() {
        Dao dao = ioc.get(Dao.class);
        
        dao.create(PojoSql.class, true);
        PojoSql pojo = new PojoSql();
        pojo.setName(R.UU32());
        pojo.setCt(System.currentTimeMillis());
        dao.insert(pojo);
        
        List<PojoSql> list = dao.query(PojoSql.class, null);
        System.out.println(Json.toJson(list, JsonFormat.full()));
    }
    
    @Test
    public void test_baas_table_to_pojo() {
        System.out.println(Cnd.where("abc", "like", "a"));
        System.out.println(Cnd.where("abc", "like", "abc"));
    }
    
//    final static public void main(String _就是要装逼啊啊啊啊啊_有神马不可呢 []) {
//        int slen = 4*60+47;
//        int dlen = 20;
//        int count = slen / dlen;
//        int pos = 540;
//        for (int i = 0; i < count; i++) {
//            int ss = i*dlen+3;
//            // -acodec copy -ss 00:02:00.00 -y -maxrate 6000k -bufsize 3000k -preset veryslow -t 00:00:20.00 -r 24 -vf crop=1920:1080:0:540 _1920_1080_0_540.mp4
//            String cmd = "nohup ffmpeg -i source.mp4 -y -maxrate 6000k -bufsize 3000k -preset veryfast -r 24 -ss "+String.format("%02d:%02d.00", ss/60, ss%60);
//            cmd += " -t 00:00:20.00 ";
//            System.out.println(cmd + " -vf crop=1920:1080:0:"+pos+" _f"+ss+"_1920_1080_0_"+pos+".mp4");
//            System.out.println(cmd + " -vf crop=1920:1080:1920:"+pos+" _f"+ss+"_1920_1080_1920_"+pos+".mp4");
//        }
//    }
//    
//    @Test
//    public void test_json_field_date_format() {
//        PojoSql pojo = new PojoSql();
//        pojo.setCreateTime(new Date());
//        System.out.println(Json.toJson(pojo));
//    }
//    
//    @Test
//    public void test_sql_comment() {
//        FileSqlManager manager = new FileSqlManager("sqls/");
//        Sql sql = manager.create("user.create");
//        System.out.println(sql);
//    }
//    
//    @Test
//    public void test_reg_http() {
//        String p = "^(http[s]?://[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)?(\\:[0-9]+)?)";
//        Pattern pattern = Pattern.compile(p);
//        String url = "http://github.com:8080/abc";
//        System.out.println(pattern.matcher(url).find());
//        url = "https://github.com:8080/abc";
//        System.out.println(pattern.matcher(url).find());
//        url = "http://localhost:8080/abc";
//        System.out.println(pattern.matcher(url).find());
//    }
//    
//    @Test
//    public void test_out_number_jpgs() throws Exception {
//        int count = 24*6;
//        int w = 1920;
//        int h = 1080;
//        Font font = Font.createFont(0, new File("D:\\msyh.ttc")).deriveFont(Font.BOLD, 200);
//        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2d = image.createGraphics();
//        g2d.setFont(font);
//        for (int i = 0; i < count; i++) {
//            int r = (int)(Math.random()*255);
//            int g = (int)(Math.random()*255);
//            int b = (int)(Math.random()*255);
//            g2d.setColor(new Color(r, g, b));
//            g2d.fillRect(0, 0, w, h);
//            g2d.setColor(Color.RED);
//            for (int j = 0; j < 2; j++) {
//                for (int k = 0; k < 2; k++) {
//                    g2d.drawString(""+(i+1), w/2*j+w/4-200, h/2*k+j/4-200);
//                }
//            }
//            Images.write(image, new File("D:\\tmp\\"+String.format("%03d", i)+".png"));
//        }
//    }
//    
//    @Test
//    public void test_tomcat_jar_npe() throws MalformedURLException {
//        new URL("file:/D:/nutzbook/apache-tomcat-8.0.28/bin/bootstrap.jar");
//    }
//    
//    @Test
//    public void test_groupby() throws MalformedURLException {
//        Pattern MULTI = Pattern.compile("^([0-9]-[0-9])(_)?([0-9]+x[0-9]+)?(~.*)$");
//        String str = "2-2_1920x1080~src.mp4";
//        Matcher ma = MULTI.matcher(str);
//        if (ma.find()) {
//            System.out.println(ma.group(1));
//            //System.out.println(ma.group(2));
//            System.out.println(ma.group(3));
//        }
//        System.out.println("...........");
//    }
//    
//    @Test
//    public void test_get_class_for_jar() throws IOException {
//        String path = Nutz.class.getName().replace('.', '/')+".class";
//        System.out.println(path);
//        Enumeration<URL> en = getClass().getClassLoader().getResources(path);
//        while (en.hasMoreElements()) {
//            System.out.println(en.nextElement());
//        }
//    }
//    
//    @Test
//    public void test_ioc_var_args() throws IOException {
//        ioc.get(TestClassArray.class);
//    }
//    
//    @Test
//    public void test_ffmpeg() throws IOException {
//        Lang.execOutput("ffmpeg -i D:\\out.amr -y D:\\out.mp3", Encoding.CHARSET_UTF8);
//    }
//    
//    @Test
//    public void test_http_session() throws IOException {
//        Request req;
//        Response resp;
//        Cookie cookie;
//        
//        // 开始登陆
//        NutMap params = new NutMap("loginName", "admin").setv("loginPassword", "123456");
//        req = Request.create("http://127.0.0.1:8080/shiro/login", METHOD.POST, params);
//        resp = Sender.create(req).send();
//        assertEquals(302, resp.getStatus());
//        cookie = resp.getCookie();
//        
//        // 访问一下需要登陆才能访问的页面
//        req = Request.create("http://127.0.0.1:8080/shiro/session/list", METHOD.GET);
//        resp = Sender.create(req).setInterceptor(cookie).send();
//        
//        // 200代表成功, 没有被302
//        assertEquals(200, resp.getStatus());
//        System.out.println(resp.getContent());
//    }
//    
//    public String readUtil(Reader r, String ends) throws IOException {
//        // 缓存各种变量
//        StringBuilder sb = new StringBuilder();
//        int endSize = ends.length();
//        char endChar = ends.charAt(endSize-1);
//        char[] buf = new char[1];
//        int len;
//        int sbLen;
//        while (true) {
//            len = r.read(buf);
//            if (len == -1)
//                break;
//            if (len == 0)
//                continue;
//            sb.append(buf[0]);
//            sbLen = sb.length();
//            // 如果读取到结束字符串的最后一个字符，那么匹配一下.事实上是endsWith的优化版本，避免生成大字符串
//            if (buf[0] == endChar && sbLen > endSize) {
//                String tmp = sb.substring(sbLen - endSize);
//                if (tmp.equals(ends)) {
//                    return sb.substring(0, sbLen - endSize);
//                }
//            }
//        }
//        return null;
//    }
    
    @Test
    public void test_delete_list() {
        UserService userService = ioc.get(UserService.class);
        String[] names = new String[]{R.UU32(), R.UU32(), R.UU32()};
        List<User> list = new ArrayList<>();
        for (String name : names) {
            User user = userService.add(name, R.UU32());
            list.add(user);
        }
        Dao dao = ioc.get(Dao.class);
        dao.delete(list);
    }
    
    @Before
    public void before() {
        CacheManager.create();
    }
    
    @Test
    public void t6() throws ExecutionException, InterruptedException {
        Sender.setup(null);
        Stopwatch sw = Stopwatch.begin();
        int taskSize = 10000;
        AtomicLong atom = new AtomicLong();
        for (int i = 0; i < taskSize; i++) {
            int t = i;
            Sender.create("http://any.nutz.cn").send(new Callback<Response>() {
                public void invoke(Response obj) {
                    System.out.println("t="+t);
                    atom.incrementAndGet();
                    obj.getContent();
                }
            });
        }
        while (atom.get() != taskSize) {
            Lang.quiteSleep(100);
        }
        sw.stop();
        System.out.println(String.format("总用时：" + sw.toString()));
    }
}
