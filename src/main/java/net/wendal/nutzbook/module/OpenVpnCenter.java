package net.wendal.nutzbook.module;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.openvpn.OpenvpnClient;

@IocBean(create="init")
@At("/openvpn")
@Fail("http:500")
public class OpenVpnCenter extends BaseModule {
	
	private static final Log log = Logs.get();
	
	@Inject("java:$conf.get('openvpn.godkey')")
	protected String godkey;

	@At({"/", "/index"})
	//@RequiresPermissions("openvpn:index")
	@Ok("ftl:/templates/admin2/openvpn/clients")
	public Context index(@Attr(value="me", scope=Scope.SESSION)int userId){
		return Lang.context().set("me", dao.fetch(UserProfile.class, userId));
	}
	
	@At
	public synchronized Object download(String macid, String platform, String key) {
		if (Strings.isBlank(macid) || Strings.isBlank(platform) || Strings.isBlank(key)) {
			return HTTP_403;
		}
		OpenvpnClient cnf = dao.fetch(OpenvpnClient.class, Cnd.where("macid", "=", macid).and("platform", "=", platform));
		if (cnf != null) {
			if (cnf.getStatus() != 1) {
				cnf.setStatus(1);
				dao.update(cnf, "status");
			}
			return _download(cnf.getFile());
		}
		if (key.equals(godkey)) {
			cnf = dao.fetch(OpenvpnClient.class, Cnd.where("status", "=", 0));
			if (cnf == null) {
				log.error("ALL OpenVPN client config is USED!!");
				return HTTP_502;
			}
			return _download(cnf.getFile());
		}
		log.debug("bad key=" + key);
		return HTTP_403;
	}
	
	public void init() throws Exception {
		super.init();
		if (Strings.isBlank(godkey))
			godkey = "123456";
		log.debug("OpenVPN godkey="+godkey);
	}
}
