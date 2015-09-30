package net.wendal.nutzbook.service;

import java.util.Date;

import net.wendal.nutzbook.annotation.SLog;
import net.wendal.nutzbook.bean.User;
import net.wendal.nutzbook.bean.UserProfile;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.service.IdNameEntityService;

@IocBean(fields="dao")
@SLog(tag="用户管理", msg="")
public class UserService extends IdNameEntityService<User> {

	@SLog(tag="新增用户", msg="用户名[${args[0]}]")
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
		if(_pass.equalsIgnoreCase(user.getPassword())) {
			return user.getId();
		}
		return -1;
	}
	
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
}
