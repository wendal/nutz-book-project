package net.wendal.nutzbook.shiro;

import java.io.Serializable;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.nutz.lang.random.R;

public class NutzbookSessionIdGenerator implements SessionIdGenerator {

	public Serializable generateId(Session session) {
		return R.UU32();
	}

}
