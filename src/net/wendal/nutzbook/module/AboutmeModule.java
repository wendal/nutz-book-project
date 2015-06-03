package net.wendal.nutzbook.module;

import java.io.File;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;

import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/u")
public class AboutmeModule extends BaseModule {
	
	@Ok("raw:jpg")
	@At("/?/avatar")
	@GET
	public Object readUserAvatar(String name, HttpServletRequest req) throws SQLException {
		User user = dao.fetch(User.class, name);
		if (user != null) {
			UserProfile profile = Daos.ext(dao, FieldFilter.create(UserProfile.class, "^avatar$")).fetch(UserProfile.class, user.getId());
			if (profile != null && profile.getAvatar() != null) {
				return profile.getAvatar();
			}
		}
		return new File(req.getServletContext().getRealPath("/rs/user_avatar/none.jpg"));
	}
}
