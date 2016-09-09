package net.wendal.nutzbook.module;

import java.io.File;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;

import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

import net.wendal.nutzbook.bean.UserProfile;
import net.wendal.nutzbook.bean.openvpn.OpenvpnClient;
import net.wendal.nutzbook.util.Toolkit;

@Api(name="OpenVPN管理", description="管理和分发OpenVPN配置文件", match=ApiMatchMode.NONE)
@IocBean(create="init")
@At("/openvpn")
@Fail("http:500")
public class OpenVpnCenter extends BaseModule {
	
	private static final Log log = Logs.get();
	
	@Inject("java:$conf.get('openvpn.godkey')")
	protected String godkey;
	
	@Inject("java:$conf.get('openvpn.dir')")
	protected String dir;

	@At({"/", "/index"})
	//@RequiresPermissions("openvpn:index")
	@RequiresRoles("admin")
	@RequiresUser
	@Ok("ftl:/templates/admin2/openvpn/clients")
	public Context index(){
		int userId = Toolkit.uid();
		return Lang.context().set("me", dao.fetch(UserProfile.class, userId));
	}
	
	@RequiresRoles("admin")
	@RequiresUser
	@At
	@AdaptBy(type=WhaleAdaptor.class)
	public void upload(@Param("file")TempFile tmp, @Param("platform")String platform) throws Exception {
		if (tmp == null)
			return;
		if (tmp.getSize() == 0) {
			tmp.delete();
			return;
		}
		if (Strings.isBlank(platform))
			platform = "win32";
		TarArchiveInputStream ins = new TarArchiveInputStream(tmp.getInputStream());
		TarArchiveEntry en = null;
		while (null != (en = ins.getNextTarEntry())) {
			String name = en.getName();
			byte[] buf = new byte[ins.available()];
			ins.read(buf);
			File f = new File(dir, name);
			Files.write(f, buf);
			String ip = Files.getMajorName(f).replace('_', '.');
			if (dao.count(OpenvpnClient.class, Cnd.where("ip", "=", ip)) == 0) {
				OpenvpnClient cnf = new OpenvpnClient();
				cnf.setIp(ip);
				cnf.setFile(f.getAbsolutePath());
				cnf.setStatus(0);
				cnf.setPlatform(platform);
				dao.insert(cnf);
			}
		}
		tmp.delete();
		ins.close();
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
			cnf.setMacid(macid);
			cnf.setStatus(1);
			dao.update(cnf, "macid|status");
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
