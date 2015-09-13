package net.wendal.nutzbook.module;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wendal.nutzbook.bean.doodle.Doodle;
import net.wendal.nutzbook.bean.doodle.DoodleItem;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;

/**
 * 简单的日期
 * 
 * @author wendal
 * 
 *         基本流程: 创建页面 要求输入 标题, 选择日期 (可选: 描述,创建者,邮箱) 详情页 显示标题(可选:描述/创建者) 可用日期
 */
@IocBean
@At("/doodle")
@Ok("json:{locked:'adminToken'}")
@Fail("http:500")
public class DoodleModule extends BaseModule {

	private static final Log log = Logs.get();
	
	@At({"/", "/v/?"})
	@Ok("jsp:jsp.doodle")
	public String page(String id) {
		return Strings.sBlank(id);
	}

	/*
	 * 新建一个doodle,数据必须是json传过来
	 */
	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	public Object create(@Param("..") Doodle doodle) {
		// TODO 校验数据
		if (doodle == null || Strings.isBlank(doodle.getTitle()))
			return ajaxFail("数据不完整");

		doodle.setAdminToken(R.UU32() + R.sg(5).next());

		doodle = dao.insert(doodle);

		if (!Strings.isBlank(doodle.getEmail())) {
			try {
				String url = Mvcs.getReq().getRequestURL().toString();
				url = url.substring(0, url.length() - 6); // 移除末尾的/create
				// TODO 异步发送邮件
				Context ctx = Lang.context(new NutMap().setv("url", url).setv("id", doodle.getId()).setv("token", doodle.getAdminToken()));
				String msg = new CharSegment("浏览网址 ${url}/${id}\n管理地址 ${url}/${id}/${token}").render(ctx).toString();
				emailService.send(doodle.getEmail().trim(), "[Nutzbook-Doodle]-" + doodle.getTitle(), msg);
			} catch (Throwable e) {
				log.debug("发送邮件失败了->" + doodle.getEmail(), e);
			}
		}

		return ajaxOk(doodle.getId());
	}

	@At("/?")
	public Object view(String id) {
		Doodle doodle = dao.fetch(Doodle.class, id);
		if (doodle == null)
			return ajaxFail("不存在指定的doodle");
		return ajaxOk(dao.fetchLinks(doodle, null));
	}

	@At("/?/?")
	public Object admin(String id, String token, HttpServletRequest req) {
		Doodle doodle = dao.fetch(Doodle.class, Cnd.where("id", "=", id).and("adminToken", "=", token));
		if (doodle == null)
			return ajaxFail("不存在指定的doodle或token不符");
		req.setAttribute("Doodle-Admin", "1");
		return ajaxOk(dao.fetchLinks(doodle, null));
	}

	@At("/?/?/update")
	@Ok("jsp:jsp.doodle.admin")
	public Object adminUpdate(String id, String token, @Param("..") Doodle doodle) {
		if (1 != dao.count(Doodle.class, Cnd.where("id", "=", id).and("adminToken", "=", token)))
			return HttpStatusView.HTTP_404;
		doodle.setUpdateTime(new Date());
		dao.update(doodle, "title|description|location|updateTime");
		return doodle;
	}

	@At("/?/invite")
	public Object invite(String id, @Param("email") String email) {
		if (Strings.isBlank(email))
			return ajaxFail("email不能为空");
		Doodle doodle = dao.fetch(Doodle.class, id);
		if (doodle == null)
			return ajaxFail("不存在指定的doodle");
		try {
			String url = Mvcs.getReq().getRequestURL().toString();
			url = url.substring(0, url.length() - 7); // 移除末尾的/invite
			// TODO 异步发送邮件
			Context ctx = Lang.context(new NutMap().setv("url", url));
			String msg = new CharSegment("浏览网址 ${url}").render(ctx).toString();
			emailService.send(email.trim(), "[Nutzbook-Doodle]-" + doodle.getTitle(), msg);
			return ajaxOk(null);
		} catch (Throwable e) {
			log.debug("发送邮件失败了->" + doodle.getEmail(), e);
			return ajaxFail(null);
		}
	}
	
	static String DU = "Doodle-User";

	@At("/?/update")
	public Object addItem(String doodleId, @Param("..") DoodleItem item, HttpServletRequest req, HttpServletResponse resp) {
		if (0 == dao.count(Doodle.class, Cnd.where("id", "=", doodleId))) {
			return ajaxFail("指定的Doodle不存在");
		}
		item.setDoodleId(doodleId);
		for (Cookie cookie : req.getCookies()) {
			if (DU.equals(cookie.getName())) {
				String uid = cookie.getValue();
				DoodleItem origin = dao.fetch(DoodleItem.class, Cnd.where("doodleId", "=", doodleId).and("cookie", "=", uid));
				if (origin != null) {
					item.setId(origin.getName());
					item.setDoodleId(doodleId);
					dao.update(item);
					return ajaxOk(null);
				}
			}
		}
		item.setCookie(R.UU32());
		dao.insert(item);
		Cookie cookie = new Cookie(DU, item.getCookie());
		cookie.setMaxAge(30*24*3600);
		cookie.setPath(req.getContextPath());
		resp.addCookie(cookie);
		return ajaxOk(null);
	}
}
