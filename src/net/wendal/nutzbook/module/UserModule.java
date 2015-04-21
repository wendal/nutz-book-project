package net.wendal.nutzbook.module;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.service.UserService;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@IocBean // 声明为Ioc容器中的一个Bean
@At("/user") // 整个模块的路径前缀
@Ok("json:{locked:'password|salt',ignoreNull:true}") // 忽略password和salt属性,忽略空属性的json输出
@Fail("http:500") // 抛出异常的话,就走500页面
public class UserModule extends BaseModule {
	
	@Inject protected UserService userService;
	
	@At
	public int count() { // 统计用户数的方法,算是个测试点
		return dao.count(User.class);
	}
	
	@GET
	@At("/login")
	@Ok("jsp:jsp.user.login")
	public void loginPage() {}

	@RequiresUser
	@At
	public Object add(@Param("..")User user) { // 两个点号是按对象属性一一设置
		NutMap re = new NutMap();
		String msg = checkUser(user, true);
		if (msg != null){
			return re.setv("ok", false).setv("msg", msg);
		}
		user = userService.add(user.getName(), user.getPassword());
		return re.setv("ok", true).setv("data", user);
	}

	@RequiresUser
	@At
	public Object update(@Param("password")String password, @Param("id")int userId) {
		if (Strings.isBlank(password) || password.length() < 6)
			return new NutMap().setv("ok", false).setv("msg", "密码不符合要求");
		userService.updatePassword(userId, password);
		return new NutMap().setv("ok", true);
	}

	@RequiresUser
	@At
	@Aop(TransAop.READ_COMMITTED)
	public Object delete(@Param("id")int id, @Attr("me")int me) {
		if (me == id) {
			return new NutMap().setv("ok", false).setv("msg", "不能删除当前用户!!");
		}
		dao.delete(User.class, id); // 再严谨一些的话,需要判断是否为>0
		dao.clear(UserProfile.class, Cnd.where("userId", "=", me));
		return new NutMap().setv("ok", true);
	}

	@RequiresUser
	@At
	public Object query(@Param("name")String name, @Param("..")Pager pager) {
		Cnd cnd = Strings.isBlank(name)? null : Cnd.where("name", "like", "%"+name+"%");
		QueryResult qr = new QueryResult();
		qr.setList(dao.query(User.class, cnd, pager));
		pager.setRecordCount(dao.count(User.class, cnd));
		qr.setPager(pager);
		return qr; //默认分页是第1页,每页20条
	}
	
	@RequiresUser
	@At("/")
	@Ok("jsp:jsp.user.list") // 真实路径是 /WEB-INF/jsp/user/list.jsp
	public void index() {
	}
	
	protected String checkUser(User user, boolean create) {
		if (user == null) {
			return "空对象";
		}
		if (create) {
			if (Strings.isBlank(user.getName()) || Strings.isBlank(user.getPassword()))
				return "用户名/密码不能为空";
		} else {
			if (Strings.isBlank(user.getPassword()))
				return "密码不能为空";
		}
		String passwd = user.getPassword().trim();
		if (6 > passwd.length() || passwd.length() > 12) {
			return "密码长度错误";
		}
		user.setPassword(passwd);
		if (create) {
			int count = dao.count(User.class, Cnd.where("name", "=", user.getName()));
			if (count != 0) {
				return "用户名已经存在";
			}
		} else {
			if (user.getId() < 1) {
				return "用户Id非法";
			}
		}
		if (user.getName() != null)
			user.setName(user.getName().trim());
		return null;
	}
	
	@At
	@Fail("jsp:jsp.500")
	@RequiresUser
	public void error() {
		throw new RuntimeException();
	}
}
