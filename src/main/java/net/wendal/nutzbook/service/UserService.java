package net.wendal.nutzbook.service;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.Date;
import java.util.List;

import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.page.Pagination;
import net.wendal.nutzbook.util.RedisKey;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.plugins.slog.annotation.Slog;
import org.nutz.service.IdNameEntityService;

@IocBean(fields = "dao")
@Slog(tag = "用户管理")
public class UserService extends IdNameEntityService<User> implements RedisKey {

	@Slog(tag = "新增用户", before = "用户名[${name}]")
	public User add(String name, String password) {
		User user = new User();
		user.setName(name.trim().toLowerCase());
		user.setSalt(R.UU16());
		user.setPassword(new Sha256Hash(password, user.getSalt()).toHex());
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		user = dao().insert(user);
		UserProfile profile = new UserProfile();
		profile.setUserId(user.getId());
		profile.setLoginname(user.getName());
		profile.setNickname(user.getName());
		dao().insert(profile);
		return user;
	}

	public int fetch(String username, String password) {
		User user = fetch(username);
		if (user == null) {
			return -1;
		}
		String _pass = new Sha256Hash(password, user.getSalt()).toHex();
		if (_pass.equalsIgnoreCase(user.getPassword())) {
			return user.getId();
		}
		return -1;
	}

	@Slog(tag = "用户更新密码", before = "用户名[${userId}]")
	public void updatePassword(int userId, String password) {
		User user = fetch(userId);
		if (user == null) {
			return;
		}
		user.setSalt(R.UU16());
		user.setPassword(new Sha256Hash(password, user.getSalt()).toHex());
		user.setUpdateTime(new Date());
		dao().update(user, "^(password|salt|updateTime)$");
	}
	
	public boolean checkPassword(User user, String password) {
		String face = new Sha256Hash(password, user.getSalt()).toHex();
		return face.equalsIgnoreCase(user.getPassword());
	}

	public Pagination getListByPager(int pageNumber) {
		Pager pager = dao().createPager(pageNumber, 20);
		List<User> list = dao().query(getEntityClass(), null, pager);
		pager.setRecordCount(dao().count(getEntityClass(), null));
		return new Pagination(pageNumber, 20, pager.getRecordCount(), list);
	}

	public User fetch(int id) {
		User user = dao().fetch(getEntityClass(), Cnd.where("id", "=", id));
		if (!Lang.isEmpty(user)) {
			dao().fetchLinks(user, "roles");
		}
		return user;
	}
	
    @Aop("redis")
    public int getUserScore(int userId) {
        Double score = jedis().zscore(RKEY_USER_SCORE, ""+userId);
        if (score == null) {
            return 0;
        } else {
            return score.intValue();
        }
    }
}
